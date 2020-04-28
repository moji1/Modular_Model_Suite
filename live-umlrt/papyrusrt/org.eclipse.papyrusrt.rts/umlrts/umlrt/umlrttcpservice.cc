// umlrttcpservice.cc

#include "basedebug.hh"
#include "basefatal.hh"
#include "umlrtoutsignal.hh"
#include "umlrttcpservice.hh"
#include "umlrttcpprotocol.hh"
#include "umlrtcontroller.hh"

#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

UMLRTHashMap * UMLRTTCPService::portSocketMap = NULL;
UMLRTHashMap * UMLRTTCPService::portIdMap = NULL;
UMLRTQueue UMLRTTCPService::outQueue;
UMLRTQueue UMLRTTCPService::acceptRequests;

UMLRTHashMap * UMLRTTCPService::getPortSocketMap ( )
{
    if (portSocketMap == NULL)
    {
    	portSocketMap = new UMLRTHashMap("portSocketMap", UMLRTHashMap::compareString, false/*objectIsString*/);
    }
    return portSocketMap;
}

UMLRTHashMap * UMLRTTCPService::getPortIdMap ( )
{
    if (portIdMap == NULL)
    {
    	portIdMap = new UMLRTHashMap("portIdMap", UMLRTHashMap::compareString, false/*objectIsString*/);
    }
    return portIdMap;
}

int UMLRTTCPService::getSocket ( const UMLRTCommsPort * port )
{
	char * id = new char[strlen(port->slotName()) + strlen(port->getName()) + 2];
	strcpy(id, port->slotName());
	strcat(id, ":");
	strcat(id, port->getName());

	UMLRTHashMap * portSocketMap = getPortSocketMap();
	int * sockPtr = (int*) portSocketMap->getObject(id);
	delete id;

	if(sockPtr == NULL)
		return -1;
	return *sockPtr;
}

void UMLRTTCPService::putSocket ( const UMLRTCommsPort * port, int socket )
{
	char * id = new char[strlen(port->slotName()) + strlen(port->getName()) + 2];
	strcpy(id, port->slotName());
	strcat(id, ":");
	strcat(id, port->getName());

	getPortSocketMap()->insert(id, (void *)new int(socket));
	getPortIdMap()->insert(id, (void *)port);
}

void UMLRTTCPService::removeSocket ( const UMLRTCommsPort * port )
{
	char * id = new char[strlen(port->slotName()) + strlen(port->getName()) + 2];
	strcpy(id, port->slotName());
	strcat(id, ":");
	strcat(id, port->getName());

	getPortSocketMap()->remove(id);
	getPortIdMap()->remove(id);
	delete id;
}

bool UMLRTTCPService::connekt ( const UMLRTCommsPort * port, const char* host, int tcpPort )
{
	int sock = getSocket(port);
	if(sock != -1)
		return false;

	sock = socket(AF_INET, SOCK_STREAM, 0);
	putSocket(port, sock);

	struct sockaddr_in addr;
	memset(&addr, '0', sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_port = htons(tcpPort);

	if(inet_pton(AF_INET, host, &addr.sin_addr) <= 0)
		return false;

    UMLRTOutSignal signal;
	int ret = connect(sock, (struct sockaddr *)&addr, sizeof(addr));
	if( ret < 0 )
		signal.initialize( "error", UMLRTTCPProtocol::signal_error, port, &UMLRTTCPProtocol::payload_error, &errno);
	else
		signal.initialize( "connected", UMLRTTCPProtocol::signal_connected, port, &UMLRTTCPProtocol::payload_connected, (void *)new int(sock));
	port->slot->controller->deliver( port, signal, port->roleIndex );

	return true;
}

bool UMLRTTCPService::disconnect ( const UMLRTCommsPort * port )
{
	UMLRTOutSignal signal;
	int socket = getSocket(port);
	if(socket == -1)
		return false;

	close(socket);
	removeSocket(port);
	signal.initialize( "disconnected", UMLRTTCPProtocol::signal_disconnected, port, &UMLRTTCPProtocol::payload_disconnected);
	port->slot->controller->deliver( port, signal, port->roleIndex );
	return true;
}

bool UMLRTTCPService::listn ( const UMLRTCommsPort * port, int tcpPort )
{
	int sock = getSocket(port);
	if(sock != -1)
		return false;

	sock = socket(AF_INET, SOCK_STREAM, 0);
	fcntl(sock, F_SETFL, O_NONBLOCK);

	struct sockaddr_in servaddr, clientaddr;
	memset(&servaddr, '0', sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(tcpPort);

	if (bind(sock, (struct sockaddr*)&servaddr, sizeof(servaddr)) != 0)
		return false;

	if (listen(sock, 1) != 0)
		return false;

	putSocket(port, sock);
	return true;
}

bool UMLRTTCPService::akcept ( const UMLRTCommsPort * port )
{
	int sock = getSocket(port);
	if(sock == -1)
		return false;

	UMLRTTCPService::AcceptRequest * request = new UMLRTTCPService::AcceptRequest;
	request->port = port;
	request->socket = sock;
	acceptRequests.enqueue(request);
	return true;
}

bool UMLRTTCPService::attach ( const UMLRTCommsPort * port, int sockfd )
{
	int sock = getSocket(port);
	if(sock == -1) {
		putSocket(port, sockfd);
		return true;
	}

	return false;
}

bool UMLRTTCPService::send ( const UMLRTCommsPort * port, char * payload, int length )
{
	int sock = getSocket(port);
	if(sock == -1)
		return false;

	UMLRTTCPService::Message * message = new UMLRTTCPService::Message;
	message->port = port;
	message->length = length;
	message->payload = payload;
	outQueue.enqueue(message);
	return true;
}

bool UMLRTTCPService::send ( const UMLRTCommsPort * port, const char* msg )
{
	int sock = getSocket(port);
	if(sock == -1)
		return false;

	UMLRTTCPService::Message * message = new UMLRTTCPService::Message;
	message->port = port;
	message->length = strlen(msg);
	message->payload = (char*)msg;
	outQueue.enqueue(message);
	return true;
}

void UMLRTTCPService::spawn ( )
{
    start(NULL);
}

// Main loop
void * UMLRTTCPService::run ( void * args )
{
	UMLRTQueue toDisconnect;

    while(1) {
    		UMLRTTCPService::Message* msg;
		while((msg = (UMLRTTCPService::Message*) outQueue.dequeue()) != NULL) {
			int socket = getSocket(msg->port);
			if(socket == -1)
				FATAL("TCP socket not found for port %s", msg->port->getName());
			int ret = write(socket, msg->payload, msg->length);
			if (ret  < 0 )
			{
				UMLRTOutSignal signal;
				signal.initialize( "error", UMLRTTCPProtocol::signal_error, msg->port, &UMLRTTCPProtocol::payload_error, &errno);
				msg->port->slot->controller->deliver( msg->port, signal, msg->port->roleIndex );
			}
		}

		getPortIdMap()->lock();
		UMLRTHashMap::Iterator clIter = getPortIdMap()->getIterator();

		while (clIter != clIter.end())
		{
			char * id  = (char *) clIter.getKey();
			const UMLRTCommsPort * port = (const UMLRTCommsPort *) clIter.getObject();
			int socket = getSocket(port);
			if(socket == -1)
				FATAL("TCP socket not found for port %s", msg->port->getName());

			static char* buffer = new char[TCP_BUFFER_SIZE];
			int bytes = recv(socket, buffer, TCP_BUFFER_SIZE, MSG_DONTWAIT);
			if(bytes > 0) {
				char* payload = strndup(buffer, bytes);
				UMLRTOutSignal signal;
				signal.initialize( "received", UMLRTTCPProtocol::signal_received, port, &UMLRTTCPProtocol::payload_received, &payload, &bytes);
				port->slot->controller->deliver( port, signal, port->roleIndex );
				delete payload;
			} else if(bytes == -1 && errno != ENOTCONN && errno != EWOULDBLOCK && errno != EAGAIN) {
				UMLRTOutSignal signal;
				signal.initialize( "error", UMLRTTCPProtocol::signal_error, port, &UMLRTTCPProtocol::payload_error, &errno);
				port->slot->controller->deliver( port, signal, port->roleIndex );
			} else if(bytes == 0) {
				UMLRTTCPService::PortElement * pe = new UMLRTTCPService::PortElement;
				pe->port = port;
				toDisconnect.enqueue(pe);
			}

			clIter = clIter.next();
		}

		getPortIdMap()->unlock();

		UMLRTTCPService::PortElement* pe;
		while((pe = (UMLRTTCPService::PortElement*) toDisconnect.dequeue()) != NULL) {
			disconnect(pe->port);
			delete pe;
		}

		UMLRTTCPService::AcceptRequest* request = (UMLRTTCPService::AcceptRequest*) acceptRequests.dequeue();
		if(request != NULL) {
			const UMLRTCommsPort * port = request->port;
			int socket = request->socket;

			struct sockaddr_in clientaddr;
			int len = sizeof(clientaddr);

			int connfd = accept(socket, (struct sockaddr*)&clientaddr, (socklen_t*) &len);
			if (connfd >= 0) {
				UMLRTOutSignal signal;
				signal.initialize( "connected", UMLRTTCPProtocol::signal_connected, port, &UMLRTTCPProtocol::payload_connected, &connfd);
				port->slot->controller->deliver( port, signal, port->roleIndex );
				delete request;
			} else {
				acceptRequests.enqueue(request);
			}
		}

		usleep(100000);
    }
}
