import React from 'react';
import SockJsClient from 'react-stomp';

const SOCKET_URL = 'http://localhost:8081/ws-message';

const WebSocketComponent = ({ onMessageReceived }) => {
    const onConnected = () => {
        console.log('Connected!!');
    };

    const handleMessage = (msg) => {
        if (onMessageReceived && typeof onMessageReceived === 'function') {
            onMessageReceived(msg);
        }
    };

    return (
        <div>
            <SockJsClient
                url={SOCKET_URL}
                topics={['/topic/message']}
                onConnect={onConnected}
                onDisconnect={() => console.log('Disconnected!')}
                onMessage={handleMessage}
                debug={false}
            />
            {/* Add any additional UI elements or logic related to WebSocket here */}
        </div>
    );
};

export default WebSocketComponent;
