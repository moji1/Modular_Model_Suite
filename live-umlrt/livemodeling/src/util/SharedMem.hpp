/*
 * SharedMem.h
 *
 *  Created on: Jun 28, 2016
 *      Author: mojtaba
 */

#ifndef SHAREDMEM_H_
#define SHAREDMEM_H_

#include <boost/interprocess/managed_shared_memory.hpp>
#include <boost/interprocess/allocators/allocator.hpp>
#include <boost/interprocess/containers/string.hpp>
#include <boost/interprocess/containers/deque.hpp>
#include <boost/interprocess/sync/scoped_lock.hpp>
#include <boost/interprocess/sync/named_mutex.hpp>

using namespace boost::interprocess;
typedef allocator<char, managed_shared_memory::segment_manager>   CharAllocator;
typedef basic_string<char, std::char_traits<char>, CharAllocator> ShmString;
typedef allocator<ShmString, managed_shared_memory::segment_manager> ShmStringAllocator;
typedef deque<ShmString, ShmStringAllocator> ShmStringDeque;
////  manage the mode that shared mem will be open
enum setupMode{client,server};
enum Status{initialized,ready,failed};

// configure allocator for saving the deque object in shared memory, we need to use the boost IPC container for that
namespace Comms
{



class SharedMem {
public:
	SharedMem(std::string name="EventArea",std::string qName="EventQ",size_t size=9999999,bool withLock=true);
	virtual ~SharedMem();

	const std::string& getName();

	int   setUp(setupMode mode=server);

	void setName(const std::string& name);
	const std::string& getQueueName() const;
	void setQueueName(const std::string& queueName);
	void pushBackString(std::string data);
	void safePushBackString(std::string data);
	void safePushFrontString(std::string data);
	void pushFrontString(std::string data);
	std::string popFrontString();
	std::string popBackString();
	std::string getData(size_t index=0);
	int  getQueueSize();
	std::string safePopFrontString();
	std::string safePopBackString();
	std::string safeGetData(size_t index=0);
	int  safeGetQueueSize();
	Status getStatus() const;
	void setStatus(Status status);
	bool isWithLock() const;
	void setWithLock(bool withLock);
	void removeSharedArea();
	size_t getSize() const;
	void setSize(size_t size);
	//void

private :
	std::string name;
	ShmStringDeque *sharedDeque;
	std::string queueName;
	managed_shared_memory observerSegment;
	bool withLock;
	Status status;
	named_mutex * areaMutex;
	size_t size;
};



}
#endif /* SHAREDMEM_H_ */
