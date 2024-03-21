import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import WebSocketComponent from './WebSocket'; // Import WebSocketComponent
import '../commons/styles/user_details.css';

class UserDetailPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            userDetails: {
                username: '',
                role: '',
            },
            devices: [],
            isLoggedIn: true,
            deviceMessages: {}, // State to store device-specific messages
        };
    }

    componentDidMount() {
        const userId = sessionStorage.getItem('userId');

        if (userId) {
            this.getUserDetails(userId).then(() => {
                this.getUserDevices();
            });
        } else {
            console.error('User ID not found in session storage');
            this.setState({isLoggedIn: false});
        }
    }
    handleGoToChat = () => {
        this.props.history.push('/chat');
    };

    async getUserDetails(userId) {
        try {
            const jwt = sessionStorage.getItem('jwt');

            const response = await fetch(`http://localhost:8080/api/user/${userId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

// Handle the response as needed

            if (response.status === 200) {
                const data = await response.json();
                this.setState({userDetails: data});
            }
        } catch (error) {
            console.error('Error fetching user details:', error);
        }
    }

    async getUserDevices() {
        const username = this.state.userDetails.username;

        try {
            const jwt = sessionStorage.getItem('jwt')
            const response = await fetch(`http://localhost:8080/api/device/devices?username=${username}`,{
                method: 'GET',
                    headers: {
                    'Authorization': `Bearer ${jwt}`,
                        'Content-Type': 'application/json'
                },
                credentials: 'include'
            });
            if (response.status === 200) {
                const data = await response.json();
                this.setState({devices: data});
            }
        } catch (error) {
            console.error('Error fetching user devices:', error);
        }
    }

    handleLogout = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/auth/signout', {
                method: 'POST',
                credentials: 'include', // Include credentials such as cookies
            });

            if (response.ok) {
                // Successfully logged out, clear session storage
                sessionStorage.removeItem('userId');
                sessionStorage.removeItem('role');

                // Redirect to the home page
                this.props.history.push('/');
            } else {
                // Handle error response if needed
                console.error('Logout failed:', response.statusText);
            }
        } catch (error) {
            console.error('Error during logout:', error);
        }
    };

    handleGoHome = () => {
        this.props.history.push('/');
    };

    handleGoToGraph = (deviceId) => {
        sessionStorage.setItem('selectedDeviceId', deviceId);
        this.props.history.push(`/historical-data/devices/${deviceId}`);
    };

    // Function to handle WebSocket messages
    onMessageReceived = (msg) => {
        console.log('Received WebSocket message in UserDetailPage:', msg.message);
        const regexResult = /Warning: Device with id: (\S+)\s*(.*)/.exec(msg.message);
        if (regexResult) {
            const [, deviceId, exceededMessage] = regexResult; // Ignore the first element which is the full match
            console.log("device id is" + deviceId);
            console.log("Exceeded message:" + exceededMessage);
            this.setState((prevState) => ({
                deviceMessages: {
                    ...prevState.deviceMessages,
                    [deviceId]: exceededMessage,
                },
            }));
        }
    };




    render() {
        const {userDetails, isLoggedIn, devices, deviceMessages} = this.state;

        return (
            <div className="container">
                {isLoggedIn ? (
                    <>
                        <h2>User Details</h2>
                        <p>Username: {userDetails.username}</p>
                        <p>Role: {userDetails.role}</p>

                        <h2>User Devices</h2>
                        <div className="device-list">
                            {devices.length > 0 ? (
                                devices.map((device) => (
                                    <div key={device.id} className="device-box">
                                        <h3>{device.name}</h3>
                                        <p><strong>Description:</strong> {device.description}</p>
                                        <p><strong>Address:</strong> {device.address}</p>
                                        <p><strong>Maximum Energy
                                            Consumption:</strong> {device.maximumEnergyConsumption}</p>
                                        <button onClick={() => this.handleGoToGraph(device.id)}>View Graph</button>
                                        {this.state.deviceMessages[device.id] && (
                                            <div className="device-message">
                                                {this.state.deviceMessages[device.id]}
                                            </div>
                                        )}
                                    </div>
                                ))
                            ) : (
                                <p>No devices connected</p>
                            )}
                        </div>
                        <button onClick={this.handleLogout}>Logout</button>
                        <button onClick={this.handleGoHome}>Home</button>
                        {/* Add a button to go to the /chat page */}
                        <button onClick={this.handleGoToChat}>Chat</button>

                        <WebSocketComponent onMessageReceived={this.onMessageReceived}/>
                    </>
                ) : (
                    <div className="error-message">
                        <p>You are not logged in</p>
                        <button onClick={() => this.props.history.push('/')}>Redirect to Home</button>
                    </div>
                )}
            </div>
        );
    }
}
    export default withRouter(UserDetailPage);