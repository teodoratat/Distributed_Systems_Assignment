import React, { useEffect, useState } from 'react';
import { over } from 'stompjs';
import SockJS from 'sockjs-client';
import { debounce } from 'lodash';

var stompClient = null;

const ChatRoom = () => {
    const [privateChats, setPrivateChats] = useState(new Map());
    const [publicChats, setPublicChats] = useState([]);
    const [tab, setTab] = useState("CHATROOM");
    const [typingUsers, setTypingUsers] = useState(new Map());
    const [users, setUsers] = useState([]); // New state for users
    const [userData, setUserData] = useState({
        username: '',
        receivername: '',
        connected: false,
        message: '',
    });

    const handleKeyDown = (event) => {
        // Call the function for every key down except for 'Enter' (to prevent sending typing notification when message is sent)
        if (event.key !== 'Enter') {
            sendTypingNotification(userData.username, tab);
        }
    };

    const sendTypingNotification = debounce((username, receiverName) => {
        const typingMessage = {
            senderName: username,
            receiverName: receiverName,
            status: "TYPING",
        };
        stompClient.send("/app/private-message", {}, JSON.stringify(typingMessage));
    }, 500);

    const handleTyping = (event) => {
        const { value } = event.target;
        setUserData({ ...userData, message: value });
        if (event.charCode !== 13) {
            sendTypingNotification(userData.username, tab);
        }
    };

    useEffect(() => {
        const storedUsername = sessionStorage.getItem('username');
        const userId = sessionStorage.getItem('userId'); // Assuming the user ID is stored in sessionStorage
        if (storedUsername && userId) {
            setUserData({ ...userData, username: storedUsername });
            connect(storedUsername);
            fetchUsers(userId); // Fetch users when the component mounts
        }
    }, []);

    const fetchUsers = async (userId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/user/allUsers/${userId}`);
            const data = await response.json();
            setUsers(data); // Update the users state with the fetched data
        } catch (error) {
            console.error("Failed to fetch users:", error);
        }
    };

    const connect = (username) => {
        let Sock = new SockJS('http://localhost:8083/ws');
        stompClient = over(Sock);
        stompClient.connect({}, () => onConnected(username), onError);
    };

    // Outside your component
    let isAlreadySubscribed = false;

    // Inside your component
    const onConnected = (username) => {
        if (!isAlreadySubscribed) {
            setUserData({ ...userData, username, connected: true });
            stompClient.subscribe('/chatroom/public', onMessageReceived);
            stompClient.subscribe('/user/' + username + '/private', onPrivateMessage);
            userJoin(username);
            isAlreadySubscribed = true; // This should be outside of state or useEffect to persist
        }
    };

    const userJoin = (username) => {
        var chatMessage = {
            senderName: username,
            status: "JOIN",
        };
        stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
    };

    const onMessageReceived = (payload) => {
        var payloadData = JSON.parse(payload.body);
        switch (payloadData.status) {
            case "JOIN":
                if (!privateChats.get(payloadData.senderName)) {
                    privateChats.set(payloadData.senderName, []);
                    setPrivateChats(new Map(privateChats));
                }
                break;
            case "MESSAGE":
                publicChats.push(payloadData);
                setPublicChats([...publicChats]);
                break;
            case "TYPING":
                setTypingUsers((prevTypingUsers) => {
                    const newTypingUsers = new Map(prevTypingUsers);
                    newTypingUsers.set(payloadData.senderName, true);
                    return newTypingUsers;
                });
                // You might want to set a timeout to remove the typing status after some time
                setTimeout(() => {
                    setTypingUsers((prevTypingUsers) => {
                        const newTypingUsers = new Map(prevTypingUsers);
                        newTypingUsers.delete(payloadData.senderName);
                        return newTypingUsers;
                    });
                }, 3000); // Remove typing status after 3 seconds
                break;
        }
    };

    const onPrivateMessage = (payload) => {
        var payloadData = JSON.parse(payload.body);

        // If the message is from the current user, don't add it because sendPrivateValue already does
        if (payloadData.senderName === userData.username) {
            return;
        }
        var nr = 0;
        // Only update the state with the new message
        setPrivateChats((prevPrivateChats) => {
            const updatedChats = new Map(prevPrivateChats);
            const senderChats = updatedChats.get(payloadData.senderName) || [];
            senderChats.push(payloadData);
            nr++;
            console.log("trm de:" + nr);
            updatedChats.set(payloadData.senderName, senderChats);
            return updatedChats;
        });
        console.log("Received private message:", payloadData);
        console.log("Current user:", userData.username); // Log the current user to verify the username state
    };

    const onError = (err) => {
        console.log(err);
    };

    const handleMessage = (event) => {
        const { value } = event.target;
        setUserData({ ...userData, message: value });
    };

    const sendValue = () => {
        if (stompClient) {
            var chatMessage = {
                senderName: userData.username,
                message: userData.message,
                status: "MESSAGE",
            };
            console.log(chatMessage);
            stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
            setUserData({ ...userData, message: "" });
        }
    };

    const sendPrivateValue = () => {
        if (stompClient) {
            const chatMessage = {
                senderName: userData.username,
                receiverName: tab,
                message: userData.message,
                status: "MESSAGE",
            };

            // Only update the state with the new message
            setPrivateChats((prevPrivateChats) => {
                const updatedChats = new Map(prevPrivateChats);
                const tabChats = updatedChats.get(tab) || [];
                tabChats.push(chatMessage);
                updatedChats.set(tab, tabChats);
                return updatedChats;
            });
            //console.log("Sending message as:", userData.username); // Log who is sending the message

            // Send the message through WebSocket
            stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage));

            // Clear the input field
            setUserData({ ...userData, message: "" });
        }
    };

    const handleUsername = (event) => {
        const { value } = event.target;
        setUserData({ ...userData, username: value });
    };

    const registerUser = () => {
        connect();
    };

    const onTyping = () => {
        const typingMessage = {
            senderName: userData.username,
            status: "TYPING",
        };
        stompClient.send("/app/message", {}, JSON.stringify(typingMessage));
    };

    return (
        <div className="container">
            {userData.connected ? (
                <div className="chat-box">
                    <div className="member-list">
                        <ul>
                            <li onClick={() => { setTab("CHATROOM") }} className={`member ${tab === "CHATROOM" && "active"}`}>Chatroom</li>
                            {Array.isArray(users) ? (
                                users.map((user, index) => (
                                    <li onClick={() => { setTab(user.username) }} className={`member ${tab === user.username && "active"}`} key={index}>{user.username}</li>
                                ))
                            ) : (
                                <li>Loading users...</li>
                            )}

                        </ul>
                    </div>
                    {tab === "CHATROOM" && (
                        <div className="chat-content">
                            <ul className="chat-messages">
                                {publicChats.map((chat, index) => (
                                    <li className={`message ${chat.senderName === userData.username && "self"}`} key={index}>
                                        {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                                        <div className="message-data">{chat.message}</div>
                                        {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                                    </li>
                                ))}
                            </ul>

                            <div className="send-message">
                                <input
                                    type="text"
                                    className="input-message"
                                    placeholder="enter the message"
                                    value={userData.message}
                                    onChange={handleMessage}
                                    onKeyDown={handleKeyDown}
                                />

                                <button type="button" className="send-button" onClick={sendValue}>send</button>
                            </div>
                        </div>
                    )}
                    {tab !== "CHATROOM" && (
                        <div className="chat-content">
                            <ul className="chat-messages">
                                {privateChats.has(tab) ?
                                    privateChats.get(tab).map((chat, index) => (
                                        <li key={index} className={`message ${chat.senderName === userData.username ? 'self' : ''}`}>
                                            {chat.senderName !== userData.username &&
                                                <div className="avatar">{chat.senderName}</div>
                                            }
                                            <div className="message-data">{chat.message}</div>
                                            {chat.senderName === userData.username &&
                                                <div className="avatar self">{chat.senderName}</div>
                                            }
                                        </li>
                                    )) :
                                    <li>No messages yet.</li>
                                }
                            </ul>

                            <div className="send-message">
                                <input
                                    type="text"
                                    className="input-message"
                                    placeholder="enter the message"
                                    value={userData.message}
                                    onChange={handleMessage}
                                    onKeyDown={handleKeyDown}
                                />
                                <button type="button" className="send-button" onClick={sendPrivateValue}>send</button>
                            </div>
                        </div>
                    )}
                    {/* Display typing notifications */}
                    <div className="typing-notifications">
                        {Array.from(typingUsers.keys()).map((username, index) => (
                            <div key={index}>{username} is typing...</div>
                        ))}
                    </div>
                </div>
            ) : (
                <div>Loading...</div>
            )}
        </div>
    );
};

export default ChatRoom;
