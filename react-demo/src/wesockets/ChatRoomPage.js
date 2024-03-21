import React, { useEffect, useState, useRef } from 'react'
import {over} from 'stompjs';
import SockJS from 'sockjs-client';
import './chat.css';
import { debounce } from 'lodash';
import { Link } from 'react-router-dom'; // Import Link from react-router-dom

var stompClient = null;

const ChatRoom = () => {
    const [privateChats, setPrivateChats] = useState(new Map());
    const [publicChats, setPublicChats] = useState([]);
    const [tab, setTab] = useState("CHATROOM");
    const [typingNotifications, setTypingNotifications] = useState([]);
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

    // Reference to keep track of typing notification timeouts
    const typingTimeouts = useRef(new Map());

    useEffect(() => {
        // Fetch data when the component mounts
        const fetchData = async () => {
            const storedUsername = await sessionStorage.getItem('username');
            const userId = await sessionStorage.getItem('userId');

            if (storedUsername && userId) {
                setUserData((prevUserData) => ({
                    ...prevUserData,
                    username: storedUsername,
                }));
                connect(storedUsername);
                fetchUsers(userId);
            }
        };

        fetchData();

        // Cleanup function to clear typing notification timeouts when the component unmounts
        return () => {
            typingTimeouts.current.forEach((timeoutId) => clearTimeout(timeoutId));
            typingTimeouts.current.clear();
        };
    }, []);


    const fetchUsers = async (userId) => {
        try {
            // Retrieve the token from wherever it is stored (e.g., sessionStorage, localStorage)
            const token = sessionStorage.getItem('jwt'); // Assuming the token is stored in sessionStorage

            // Check if the token exists
            if (!token) {
                console.error("Token not found.");
                return;
            }


            // Make the fetch request with the token included in the headers
            const response = await fetch(`http://localhost:8080/api/user/allUsers/${userId}`,
                {
                    method: 'GET',
                    credentials: 'include', // Include this line to send cookies
                    headers: {
                        'Authorization': `Bearer ${token}`, // Include your JWT token if needed

                },
            });

            // Check if the response is successful
            if (!response.ok) {
                throw new Error('Failed to fetch users');
            }

            // Parse the response JSON
            const data = await response.json();

            // Update the users state with the fetched data
            setUsers(data);
        } catch (error) {
            console.error("Failed to fetch users:", error);
        }
    };

    // This function will be triggered when the receiver clicks on a message
    const handleMessageClick = (message) => {
        if (message.senderName === userData.username || message.status === 'READ') {
            // Don't mark own messages or already read messages
            return;
        }

        // Update the message status locally
        setPrivateChats(prevChats => {
            const updatedChats = new Map(prevChats);
            const messages = updatedChats.get(message.senderName) || [];
            const updatedMessages = messages.map(msg =>
                msg.id === message.id ? { ...msg, status: 'READ' } : msg
            );
            updatedChats.set(message.senderName, updatedMessages);
            return updatedChats;
        });


        // Send a message status update to the sender
        const readNotification = {
            senderName: userData.username,
            receiverName: message.senderName,
            messageId: message.id,
            status: 'READ'
        };
        stompClient.send("/app/private-message", {}, JSON.stringify(readNotification));
    };


    const connect = (username) => {
        let Sock = new SockJS('http://localhost:8084/ws');
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
    const renderTypingNotifications = () => {
        return (
            <div className="typing-notifications">
                {Array.from(typingUsers.keys()).filter(username => {
                    // Filter to show typing notifications only for the active chat
                    return (tab === "CHATROOM" && publicChats.some(chat => chat.senderName === username))
                        || (tab !== "CHATROOM" && privateChats.has(tab) && privateChats.get(tab).some(chat => chat.senderName === username));
                }).map((username, index) => (
                    <div key={index}>{username} is typing...</div>
                ))}
            </div>
        );
    };


    const onMessageReceived = (payload) => {
        console.log("Received message:", payload.body); // Add this line
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
                    console.log("Updated typingUsers:", typingUsers);
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
            case "READ":
                // Logic to handle READ status for public messages
                // Update the message status in publicChats to "READ" if necessary
                setPublicChats(prevChats => prevChats.map(chat =>
                    chat.id === payloadData.messageId ? { ...chat, status: 'READ' } : chat
                ));
                break;
        }
    };


    /*const onPrivateMessage = (payload) => {
        var payloadData = JSON.parse(payload.body);

        // First, handle the case where the message is for the current user
        if (payloadData.receiverName === userData.username) {
            // Update the private chats state
            setPrivateChats(prevChats => {
                const updatedChats = new Map(prevChats);
                const messages = updatedChats.get(payloadData.senderName) || [];
                messages.push(payloadData);
                updatedChats.set(payloadData.senderName, messages);
                return updatedChats;
            });
        }
        // If the message is from the current user and has status "MESSAGE", update private chats
        if (payloadData.senderName === userData.username && payloadData.status === "MESSAGE") {
            setPrivateChats((prevPrivateChats) => {
                const updatedChats = new Map(prevPrivateChats);
                const senderChats = updatedChats.get(payloadData.senderName) || [];
                senderChats.push(payloadData);
                updatedChats.set(payloadData.senderName, senderChats);
                return updatedChats;
            });
        } else if (payloadData.status === "MESSAGE" && payloadData.receiverName === userData.username) {
            // If the message is for the current user, update private chats
            setPrivateChats((prevPrivateChats) => {
                const updatedChats = new Map(prevPrivateChats);
                const senderChats = updatedChats.get(payloadData.senderName) || [];
                senderChats.push(payloadData);
                updatedChats.set(payloadData.senderName, senderChats);
                return updatedChats;
            });
        } else if (payloadData.status === "TYPING") {
            // Handle "TYPING" status
            console.log(payloadData.senderName + ' is typing...');
            // Update the typingUsers state or use a similar approach
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
        }

        console.log("Received private message:", payloadData);
        console.log("Current user:", userData.username); // Log the current user to verify the username state
    };*/
//merge ok
    // const onPrivateMessage = (payload) => {
    //     const payloadData = JSON.parse(payload.body);
    //
    //     // Handle private chat messages
    //     if (payloadData.status === "MESSAGE" || payloadData.status === "READ") {
    //         setPrivateChats(prevChats => {
    //             const updatedChats = new Map(prevChats);
    //             const chatKey = payloadData.senderName === userData.username ? payloadData.receiverName : payloadData.senderName;
    //
    //             const messages = updatedChats.get(chatKey) || [];
    //             if (payloadData.status === "READ") {
    //                 // Update the status of the specific message
    //                 const updatedMessages = messages.map(msg =>
    //                     msg.id === payloadData.messageId ? { ...msg, status: 'READ' } : msg
    //                 );
    //                 updatedChats.set(chatKey, updatedMessages);
    //             } else {
    //                 // Add new message
    //                 messages.push(payloadData);
    //                 updatedChats.set(chatKey, messages);
    //             }
    //
    //             return updatedChats;
    //         });
    //     }
    //
    //     // Handle typing notifications
    //     if (payloadData.status === "TYPING") {
    //         setTypingUsers(prevTypingUsers => {
    //             const newTypingUsers = new Map(prevTypingUsers);
    //             newTypingUsers.set(payloadData.senderName, true);
    //             return newTypingUsers;
    //         });
    //
    //         // Set a timeout to remove the typing status after a few seconds
    //         setTimeout(() => {
    //             setTypingUsers(prevTypingUsers => {
    //                 const newTypingUsers = new Map(prevTypingUsers);
    //                 newTypingUsers.delete(payloadData.senderName);
    //                 return newTypingUsers;
    //             });
    //         }, 3000); // 3 seconds delay
    //     }
    //
    //     // Handle other private message types if necessary
    // };

    // const onPrivateMessage = (payload) => {
    //     const payloadData = JSON.parse(payload.body);
    //
    //     // Handle private chat messages
    //     if (payloadData.status === "MESSAGE" || payloadData.status === "READ") {
    //         setPrivateChats(prevChats => {
    //             const updatedChats = new Map(prevChats);
    //             const chatKey = payloadData.senderName === userData.username ? payloadData.receiverName : payloadData.senderName;
    //
    //             const messages = updatedChats.get(chatKey) || [];
    //             if (payloadData.status === "READ") {
    //                 // Update the status of the specific message
    //                 const updatedMessages = messages.map(msg =>
    //                     msg.id === payloadData.messageId ? { ...msg, status: 'READ' } : msg
    //                 );
    //                 updatedChats.set(chatKey, updatedMessages);
    //             } else {
    //                 // Add new message
    //                 messages.push(payloadData);
    //                 updatedChats.set(chatKey, messages);
    //             }
    //
    //             return updatedChats;
    //         });
    //     }
    //
    //     // Handle typing notifications
    //     if (payloadData.status === "TYPING") {
    //         setTypingUsers(prevTypingUsers => {
    //             const newTypingUsers = new Map(prevTypingUsers);
    //             newTypingUsers.set(payloadData.senderName, true);
    //             return newTypingUsers;
    //         });
    //
    //         // Set a timeout to remove the typing status after a few seconds
    //         setTimeout(() => {
    //             setTypingUsers(prevTypingUsers => {
    //                 const newTypingUsers = new Map(prevTypingUsers);
    //                 newTypingUsers.delete(payloadData.senderName);
    //                 return newTypingUsers;
    //             });
    //         }, 3000); // 3 seconds delay
    //     }
    //
    //     // Handle other private message types if necessary
    // };
    const onPrivateMessage = (payload) => {
        const payloadData = JSON.parse(payload.body);

        // Handle private chat messages
        if (payloadData.status === "MESSAGE" || payloadData.status === "READ") {
            setPrivateChats(prevChats => {
                const updatedChats = new Map(prevChats);
                const chatKey = payloadData.senderName === userData.username ? payloadData.receiverName : payloadData.senderName;

                // If the sender is the current user, add the message to the receiver's chat
                if (payloadData.senderName === userData.username) {
                    const messages = updatedChats.get(chatKey) || [];
                    if (payloadData.status === "READ") {
                        // Update the status of the specific message
                        const updatedMessages = messages.map(msg =>
                            msg.id === payloadData.messageId ? { ...msg, status: 'READ' } : msg
                        );
                        updatedChats.set(chatKey, updatedMessages);
                    } else {
                        // Add new message
                        messages.push(payloadData);
                        updatedChats.set(chatKey, messages);
                    }
                } else {
                    // If the sender is not the current user, add the message to the sender's chat
                    const messages = updatedChats.get(chatKey) || [];
                    if (payloadData.status === "READ") {
                        // Update the status of the specific message
                        const updatedMessages = messages.map(msg =>
                            msg.id === payloadData.messageId ? { ...msg, status: 'READ' } : msg
                        );
                        updatedChats.set(chatKey, updatedMessages);
                    } else {
                        // Add new message
                        messages.push(payloadData);
                        updatedChats.set(chatKey, messages);
                    }
                }

                return updatedChats;
            });
        }

        // Handle typing notifications
        if (payloadData.status === "TYPING") {
            setTypingUsers(prevTypingUsers => {
                const newTypingUsers = new Map(prevTypingUsers);
                newTypingUsers.set(payloadData.senderName, true);
                return newTypingUsers;
            });

            // Set a timeout to remove the typing status after a few seconds
            setTimeout(() => {
                setTypingUsers(prevTypingUsers => {
                    const newTypingUsers = new Map(prevTypingUsers);
                    newTypingUsers.delete(payloadData.senderName);
                    return newTypingUsers;
                });
            }, 3000); // 3 seconds delay
        }

        // Handle other private message types if necessary
    };



    const onError = (err) => {
        console.log(err);
    };

    const handleMessage = (event) => {
        const { value } = event.target;
        setUserData({ ...userData, message: value });
    };

    // const sendValue = () => {
    //     if (stompClient) {
    //         var chatMessage = {
    //             senderName: userData.username,
    //             message: userData.message,
    //             status: "MESSAGE",
    //         };
    //         console.log(chatMessage);
    //         stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
    //         setUserData({ ...userData, message: "" });
    //     }
    // };
    //
    // const sendPrivateValue = () => {
    //     if (stompClient) {
    //         const chatMessage = {
    //             senderName: userData.username,
    //             receiverName: tab,
    //             message: userData.message,
    //             status: "MESSAGE",
    //         };
    //
    //         // Only update the state with the new message
    //         setPrivateChats((prevPrivateChats) => {
    //             const updatedChats = new Map(prevPrivateChats);
    //             const tabChats = updatedChats.get(tab) || [];
    //             tabChats.push(chatMessage);
    //             updatedChats.set(tab, tabChats);
    //             return updatedChats;
    //         });
    //         //console.log("Sending message as:", userData.username); // Log who is sending the message
    //
    //         // Send the message through WebSocket
    //         stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage));
    //
    //         // Clear the input field
    //         setUserData({ ...userData, message: "" });
    //     }
    // };

    const sendValue = () => {
        if (stompClient && userData.message.trim() !== '') { // Check if message is not empty
            var chatMessage = {
                senderName: userData.username,
                messageContent: userData.message, // Use messageContent instead of message
                status: "MESSAGE",
            };
            console.log(chatMessage); // Log the message before sending
            stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
            setUserData((prevUserData) => ({ ...prevUserData, message: "" })); // Clear the message after sending
        }
    };

    const sendPrivateValue = () => {
        if (stompClient) {
            const chatMessage = {
                senderName: userData.username,
                receiverName: tab,
                messageContent: userData.message, // Update to messageContent if necessary
                status: "MESSAGE"
            };

            // Add the sent message to the private chat
            setPrivateChats(prevChats => {
                const updatedChats = new Map(prevChats);
                const chatKey = tab;
                const messages = updatedChats.get(chatKey) || [];
                messages.push(chatMessage);
                updatedChats.set(chatKey, messages);
                return updatedChats;
            });

            // Send the message via WebSocket
            stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage));

            // Clear the input field after sending
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

    const handleTypingNotification = (username) => {
        // Clear any existing timeout for this user
        if (typingTimeouts.current.has(username)) {
            clearTimeout(typingTimeouts.current.get(username));
            typingTimeouts.current.delete(username);
        }

        // Set the user as typing
        setTypingUsers((prev) => new Map(prev).set(username, true));

        // Set a timeout to remove the typing status
        const timeoutId = setTimeout(() => {
            setTypingUsers((prev) => {
                const newTypingUsers = new Map(prev);
                newTypingUsers.delete(username);
                return newTypingUsers;
            });
        }, 3000); // 3 seconds delay

        // Store the timeout ID
        typingTimeouts.current.set(username, timeoutId);
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
                    {/* Add the HOME button in the top-left corner */}
                    <Link to="/">
                        <button className="home-button">&#x2190;>HOME</button>
                    </Link>
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
                                    <li key={index} className={`message ${chat.senderName === userData.username ? 'self' : ''}`}>
                                        {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                                        <div className="message-data">{chat.messageContent}</div>
                                        {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                                    </li>
                                ))}
                            </ul>

                            <div className="send-message">
                                <input
                                    type="text"
                                    className="input-message"
                                    placeholder="Enter your message"
                                    value={userData.message}
                                    onChange={handleMessage}
                                    onKeyDown={handleKeyDown}
                                />
                                <button type="button" className="send-button" onClick={sendValue}>Send</button>
                            </div>
                        </div>
                    )}

                    {tab !== "CHATROOM" && (
                        <div className="chat-content">
                            {console.log("Rendering private chats for tab: ", tab, privateChats.get(tab))}
                            <ul className="chat-messages">
                                {privateChats.has(tab) ? (
                                    privateChats.get(tab).map((chat, index) => (
                                        <li key={index}
                                            className={`message ${chat.senderName === userData.username ? 'self' : ''}`}
                                            onClick={() => handleMessageClick(chat)}>
                                            {chat.senderName !== userData.username && (
                                                <div className="avatar">{chat.senderName}</div>
                                            )}
                                            <div className="message-data">
                                                {chat.messageContent}
                                                {/* Render the read receipt only if the current user is the sender and the message status is 'READ' */}
                                                {chat.senderName === userData.username && chat.status === 'READ' &&
                                                    <span className="read-receipt">✔</span>}
                                            </div>
                                            {chat.senderName === userData.username && (
                                                <div className="avatar self">{chat.senderName}</div>
                                            )}
                                        </li>
                                    ))
                                ) : (
                                    <li>No messages yet.</li>
                                )}
                            </ul>

                            <div className="send-message">
                                <input
                                    type="text"
                                    className="input-message"
                                    placeholder="Enter your message"
                                    value={userData.message}
                                    onChange={handleMessage}
                                    onKeyDown={handleKeyDown}
                                />
                                <button type="button" className="send-button" onClick={sendPrivateValue}>Send</button>
                            </div>
                        </div>
                    )}



                    {/* Display typing notifications */}
                    {renderTypingNotifications()}

                </div>
            ) : (
                <div>Loading...</div>
            )}
        </div>
    );

};

export default ChatRoom;

