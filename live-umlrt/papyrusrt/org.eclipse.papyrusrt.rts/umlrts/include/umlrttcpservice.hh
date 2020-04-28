// umlrttcpservice.hh

#ifndef UMLRTTCPSERVICE_HH
#define UMLRTTCPSERVICE_HH

#include "umlrtbasicthread.hh"
#include "umlrtqueue.hh"
#include "umlrtqueueelement.hh"
#include "umlrtcommsport.hh"
#include "umlrthashmap.hh"

#ifndef TCP_BUFFER_SIZE
#define TCP_BUFFER_SIZE 1024
#endif

class UMLRTTCPService : UMLRTBasicThread
{
public:
	UMLRTTCPService ( ) : UMLRTBasicThread("UMLRTTCPService")
	{
	}
    
    class Message : public UMLRTQueueElement {
	public:
		const UMLRTCommsPort * port;
		char * payload;
		int length;
    };

    class AcceptRequest : public UMLRTQueueElement {
	public:
		const UMLRTCommsPort * port;
		int socket;
    };

    class PortElement : public UMLRTQueueElement {
	public:
		const UMLRTCommsPort * port;
    };

    void spawn ( );

    static bool connekt ( const UMLRTCommsPort * port, const char * host, int tcpPort );
    static bool listn ( const UMLRTCommsPort * port, int tcpPort );
    static bool akcept ( const UMLRTCommsPort * port );
    static bool attach ( const UMLRTCommsPort * port, int sockfd );
    static bool disconnect ( const UMLRTCommsPort * port );
    static bool send ( const UMLRTCommsPort * port, char * payload, int length );
    static bool send ( const UMLRTCommsPort * port, const char * msg );
private:
    virtual void * run ( void * args );

    static UMLRTQueue outQueue;
    static UMLRTQueue acceptRequests;
    static UMLRTHashMap * portSocketMap;
    static UMLRTHashMap * portIdMap;

    static void queueMessage ( UMLRTTCPService::Message * msg );

    static int getSocket ( const UMLRTCommsPort * port );
    static void putSocket ( const UMLRTCommsPort * port, int socket );
    static void removeSocket ( const UMLRTCommsPort * port );
    static UMLRTHashMap * getPortSocketMap ( );
    static UMLRTHashMap * getPortIdMap ( );
};

#endif // UMLRTTCPSERVICE_HH
