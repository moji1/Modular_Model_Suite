// umlrttcpprotocol.cc

#include "umlrttcpprotocol.hh"

UMLRTObject_field UMLRTTCPProtocol::fields_connected[] =
{
    {
        "sockfd",
        &UMLRTType_int,
        offsetof( params_connected, sockfd ),
        1,
        0
    }
};

UMLRTObject UMLRTTCPProtocol::payload_connected =
{
    sizeof( params_connected ),
    1,
	fields_connected
};

UMLRTObject_field UMLRTTCPProtocol::fields_received[] =
{
    {
        "payload",
        &UMLRTType_charptr,
        offsetof( params_received, payload ),
        1,
        0
    },
    {
        "length",
        &UMLRTType_int,
        offsetof( params_received, length ),
        1,
        0
    }
};

UMLRTObject UMLRTTCPProtocol::payload_received =
{
    sizeof( params_received ),
    2,
    fields_received
};

UMLRTObject_field UMLRTTCPProtocol::fields_error[] =
{
    {
        "errno",
        &UMLRTType_int,
        offsetof( params_error, errno ),
        1,
        0
    }
};

UMLRTObject UMLRTTCPProtocol::payload_error =
{
    sizeof( params_error ),
    1,
	fields_error
};

UMLRTObject_field UMLRTTCPProtocol::fields_disconnected[] =
{
    #ifdef NEED_NON_FLEXIBLE_ARRAY
    {
        0,
        0,
        0,
        0,
        0
    }
    #endif
};

UMLRTObject UMLRTTCPProtocol::payload_disconnected =
{
    0,
    #ifdef NEED_NON_FLEXIBLE_ARRAY
    1
    #else
    0
    #endif
    ,
    fields_disconnected
};
