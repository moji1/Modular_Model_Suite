#include "SharedMem.hpp"
#include <iostream>

namespace Comms{

SharedMem::SharedMem(std::string name,std::string qName,size_t size,bool withLock)
{

	sharedDeque=0;
	areaMutex=0;
	//charallocator=0;
	this->setName(name);
	this->setQueueName(qName);
	this->status=initialized;
	this->withLock=withLock;
	this->size=size;
}
//////
 SharedMem::~SharedMem()
{
	shared_memory_object::remove(this->name.c_str());

}

 ////
size_t SharedMem::getSize() const {
		return this->size;
	}

void SharedMem::setSize(size_t size) {
		this->size = size;
	}
/////
void SharedMem::removeSharedArea()
{
	shared_memory_object::remove(this->name.c_str());
}
////
const std::string& SharedMem::getName()
{
		return name;
}
//////
int SharedMem::setUp(setupMode mode)  // setup shared memory segment, and allocate the object for read and write
{
	if (mode==server)
	{
		try
		{
			shared_memory_object::remove(this->getName().c_str()); // remove it if it is already created before
			this->observerSegment=managed_shared_memory(create_only, this->getName().c_str(),this->getSize());
			const CharAllocator charallocator (observerSegment.get_segment_manager());
			const ShmStringAllocator stringInst(observerSegment.get_segment_manager());
			this->sharedDeque = observerSegment.construct<ShmStringDeque>(this->getQueueName().c_str())(stringInst);
			/// create the mutex also for locking
			named_mutex::remove((this->getName()).c_str());
			this->areaMutex=new named_mutex(open_or_create, (this->getName()).c_str());
			this->setStatus(ready);
			return 0;
		}
		catch(boost::interprocess::interprocess_exception &ex){
		            std::cout << "The Error happened in  shared memory setup: "<<"\n";
		            this->setStatus(failed);
		            return -1;
		}

	}
	else if (mode==client)
	{
		try
		{
			this->observerSegment=managed_shared_memory(open_only, this->getName().c_str());
			// find and load related queue
			this->sharedDeque = observerSegment.find<ShmStringDeque>(this->getQueueName().c_str()).first;
			//this->sharedDeque = segment.find<ShmStringVector>("MyVector").first;
			this->setStatus(ready);
			/// create the mutex
			this->areaMutex=new named_mutex(open_only, (this->getName()).c_str());
			//this->areaMutex=new named_mutex(open_or_create, (this->getName()).c_str());
			return 0;
		}
		catch (boost::interprocess::interprocess_exception &ex)
		{
			std::cout << "The Error happened in  shared memory setup: "<<"\n";
			this->setStatus(failed);
			return -1;
		}

	}
	else
		// no other mode exist now, will extended in future
		return -1;

}

///
 void SharedMem::setName(const std::string& name)
{
	this->name = name;
}
/////
 const std::string& SharedMem::getQueueName() const {
	return queueName;
}
//////
 void SharedMem::setQueueName(const std::string& queueName) {
	this->queueName = queueName;
}
//////
 Status SharedMem::getStatus() const {
	return status;
}
//////
 void SharedMem::setStatus(Status status) {
	this->status = status;
}
//////
 bool SharedMem::isWithLock() const {
	return withLock;
}
////
 void SharedMem::setWithLock(bool withLock) {
	this->withLock = withLock;
}

 std::string SharedMem::popFrontString()
//////
{
	if (! this->sharedDeque->empty())
	{
		//const CharAllocator charallocator (observerSegment.get_segment_manager());
		//ShmString tempString(charallocator);
		//tempString= this->sharedDeque->front();
		//return std::string(tempString.begin(),tempString.end());
		//std::cout<<"reading from shared memory\n";
		std::string s1= std::string(this->sharedDeque->front().begin(),this->sharedDeque->front().end());
		//std::cout<<s1;
		this->sharedDeque->pop_front();
		return s1;

	}
	else
		return "";
}
////
 std::string SharedMem::popBackString()
{
	if (! this->sharedDeque->empty())
	{
		const CharAllocator charallocator (observerSegment.get_segment_manager());
		ShmString tempString(charallocator);
		tempString=this->sharedDeque->back();
		this->sharedDeque->pop_back();
		return std::string(tempString.begin(),tempString.end());
		//return s;
		//return "---";
	}
	else
		return "";
}
////
 std::string SharedMem::getData(size_t index)
{
	if ( this->sharedDeque->size() > index)
		{
			const CharAllocator charallocator (observerSegment.get_segment_manager());
			ShmString tempString(charallocator);
			tempString=this->sharedDeque->at(index);
			return std::string(tempString.begin(),tempString.end());
			//return "---";
		}
	else
			return "";
}

 int  SharedMem::safeGetQueueSize()
{
	{scoped_lock<named_mutex> lock(*areaMutex);
	return this->sharedDeque->size();}
}
 std::string SharedMem::safePopFrontString()
{
	{scoped_lock<named_mutex> lock(*areaMutex);
	return this->popFrontString();}
}
 std::string SharedMem::safePopBackString()
{
	{scoped_lock<named_mutex> lock(*areaMutex);
	return this->popBackString();}
}
 std::string SharedMem::safeGetData(size_t index)
{
	{scoped_lock<named_mutex> lock(*areaMutex);
	return this->getData(index);}
}

 int  SharedMem::getQueueSize()
{
	return this->sharedDeque->size();
}

 void SharedMem::pushBackString(std::string data)
{
	const CharAllocator charallocator (observerSegment.get_segment_manager());
	ShmString tempString(charallocator);
	tempString=data.c_str();
	this->sharedDeque->push_back(tempString);
}
 void SharedMem::safePushFrontString(std::string data)
{

	const CharAllocator charallocator (observerSegment.get_segment_manager());
	ShmString tempString(charallocator);
	tempString=data.c_str();
	{scoped_lock<named_mutex> lock(*areaMutex);
	this->sharedDeque->push_back(tempString);}
}
 void SharedMem::pushFrontString(std::string data)
{
	const CharAllocator charallocator (observerSegment.get_segment_manager());
	ShmString tempString(charallocator);
	tempString=data.c_str();
	this->sharedDeque->push_front(tempString);
}
 void SharedMem::safePushBackString(std::string data)
{

	const CharAllocator charallocator (observerSegment.get_segment_manager());
	ShmString tempString(charallocator);
	tempString=data.c_str();
	{scoped_lock<named_mutex> lock(*areaMutex);
	this->sharedDeque->push_front(tempString);}
}
}

