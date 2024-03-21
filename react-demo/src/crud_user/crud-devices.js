import React, { Component } from 'react';
import '../commons/styles/user_details.css'; // Import common user details styles
import { Link } from 'react-router-dom';

class DeviceListPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            devices: [],
            users: [],
            newDevice: {
                name: '',
                description: '',
                address: '',
                maximumEnergyConsumption: '',
                userId: '',
            },
            editingDeviceId: null,
            editedDevice: {
                name: '',
                description: '',
                address: '',
                maximumEnergyConsumption: '',
                userId: '',
            },
            isLoggedIn: false,
            isAdmin: false,
            errorMessage: '', // Initialize error message
        };
    }
    componentDidMount() {
        const userId = sessionStorage.getItem('userId');

        if (userId) {
            this.fetchDeviceList();
            this.fetchUserList();
            if (sessionStorage.getItem('role') === 'admin') {
                this.setState({ isLoggedIn: true, isAdmin: true });
            } else {
                this.setState({ isLoggedIn: true });
            }
        } else {
            console.error('User ID not found in session storage');
            this.setState({ isLoggedIn: false });
        }
    }

    handleInputChange(event, field) {
        const { name, value } = event.target;
        let errorMessage = ''; // Initialize or clear error message

        if (field === 'name' || field === 'description' || field === 'address' || field === 'userId') {
            this.setState((prevState) => ({
                newDevice: {
                    ...prevState.newDevice,
                    [field]: value,
                },
            }));
        } else if (field === 'maximumEnergyConsumption') {
            // Check if the input is a valid number
            if (!isNaN(value) && parseFloat(value) >= 0) {
                this.setState((prevState) => ({
                    newDevice: {
                        ...prevState.newDevice,
                        [field]: value,
                    },
                }));
            } else {
                errorMessage = 'Invalid input for maximumEnergyConsumption. Please enter a valid number.';
            }
        }

        this.setState({ errorMessage }); // Update the error message
    }

    fetchDeviceList() {
        fetch('http://localhost:8080/api/device/alll')
            .then((response) => {
                if (response.status === 200) {
                    response.json().then((data) => {
                        this.setState({ devices: data });
                    });
                }
            })
            .catch((error) => {
                console.error('Error fetching device list:', error);
            });
    }

    handleLogout = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/auth/signout', {
                method: 'POST',
                credentials: 'include', // Include credentials such as cookies
            });

            if (response.ok) {
                // Successfully logged out, clear session storage
                sessionStorage.clear();
                this.setState({ isLoggedIn: false }); // Update login status

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
    fetchUserList() {
        fetch('http://localhost:8082/device/user/all')
            .then((response) => {
                if (response.status === 200) {
                    response.json().then((data) => {
                        this.setState({ users: data });
                    });
                }
            })
            .catch((error) => {
                console.error('Error fetching user list:', error);
            });
    }

    handleInputChange(event, field) {
        const { name, value } = event.target;

        if (field === 'name' || field === 'description' || field === 'address' || field === 'userId') {
            this.setState((prevState) => ({
                newDevice: {
                    ...prevState.newDevice,
                    [field]: value,
                },
            }));
        } else if (field === 'maximumEnergyConsumption') {
            // Check if the input is a valid number
            if (!isNaN(value) && parseFloat(value) >= 0) {
                this.setState((prevState) => ({
                    newDevice: {
                        ...prevState.newDevice,
                        [field]: value,
                    },
                }));
            } else {
                console.error('Invalid input for maximumEnergyConsumption. Please enter a valid number.');
            }
        }
    }


    handleCreateDevice = () => {
        const { newDevice, users } = this.state;
        const selectedUser = users.find((user) => user.id === newDevice.userId);

        if (!selectedUser) {
            console.error('Selected user not found');
            return;
        }

        const deviceData = {
            id: '59550dcc-b245-4fba-8ae6-4775344d301a',
            name: newDevice.name,
            description: newDevice.description,
            address: newDevice.address,
            maximumEnergyConsumption: newDevice.maximumEnergyConsumption,
            user: {
                id: selectedUser.id,
                username: selectedUser.username,
            },
        };

        const  jwtToken = sessionStorage.getItem('jwt')
        fetch('http://localhost:8080/api/device/insert', {
            method: 'POST',
            credentials:'include',
            headers: {'Authorization': `Bearer ${jwtToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData),
        })
            .then((response) => {
                if (response.status === 201) {
                    this.setState((prevState) => ({
                        devices: [...prevState.devices, deviceData],
                        newDevice: {
                            name: '',
                            description: '',
                            address: '',
                            maximumEnergyConsumption: '',
                            userId: '',
                        },
                    }));
                    window.location.reload();
                } else {
                    console.error('Error creating device. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error creating device:', error);
            });
    }


    handleDeleteDevice = (deviceId) => {
        const jwtToken = sessionStorage.getItem('jwt')
        fetch(`http://localhost:8080/api/device/delete/${deviceId}`, {
            method: 'DELETE',
            credentials:'include',
           headers: {'Authorization': `Bearer ${jwtToken}`,}
        })
            .then((response) => {
                if (response.status === 200) {
                    window.location.reload();
                    this.fetchDeviceList();
                } else if (response.status === 404) {
                    console.error('Device not found');
                } else {
                    console.error('Error deleting device. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error deleting device:', error);
            });
    }

    handleEditInputChange(event, field) {
        const { value } = event.target;

        if (field === 'name' || field === 'description' || field === 'address' || field === 'userId') {
            this.setState((prevState) => ({
                editedDevice: {
                    ...prevState.editedDevice,
                    [field]: value,
                },
            }));
        } else if (field === 'maximumEnergyConsumption') {
            // Check if the input is a valid number
            if (!isNaN(value) && parseFloat(value) >= 0) {
                this.setState((prevState) => ({
                    editedDevice: {
                        ...prevState.editedDevice,
                        [field]: value,
                    },
                }));
            } else {
                this.setState({ errorMessage: 'Invalid input for maximumEnergyConsumption. Please enter a valid number.' });

            }
        }
    }


    handleSaveDevice = () => {
        const { editedDevice, users } = this.state;
        const selectedUser = users.find((user) => user.id === editedDevice.userId);

        if (!editedDevice.id) {
            console.error('Device ID is missing.');
            return;
        }

        const deviceData = {
            id: editedDevice.id,
            name: editedDevice.name,
            description: editedDevice.description,
            address: editedDevice.address,
            maximumEnergyConsumption: editedDevice.maximumEnergyConsumption,
            user: {
                id: selectedUser.id,
                username: selectedUser.username,
            },
        };

        const jwtToken = sessionStorage.getItem('jwt')
        fetch(`http://localhost:8080/api/device/update/${editedDevice.id}`, {
            method: 'PUT',
            credentials:'include',
            headers: {'Authorization': `Bearer ${jwtToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData),
        })
            .then((response) => {
                if (response.status === 200) {
                    this.setState({
                        editingDeviceId: null,
                        editedDevice: {
                            id: '',
                            name: '',
                            description: '',
                            address: '',
                            maximumEnergyConsumption: '',
                            userId: '',
                        },
                    });
                    window.location.reload();
                    this.fetchDeviceList();
                } else {
                    console.error('Error updating device. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error updating device:', error);
            });
    }
    handleEditDevice = (device) => {
        if (device) {
            this.setState({
                editingDeviceId: device.id,
                editedDevice: {
                    id: device.id,
                    name: device.name,
                    description: device.description,
                    address: device.address,
                    maximumEnergyConsumption: device.maximumEnergyConsumption,
                    userId: device.user ? device.user.id : '',
                },
            });
        }
    }
    render() {
        const { newDevice, users, editingDeviceId, editedDevice, isLoggedIn, isAdmin, errorMessage } = this.state;

        return (
            <div className="admin-user-creation">
                <div className="navigation">
                    <Link to="/user-create" className="crud-devices-button">Go to Users</Link>
                </div>
                <div className="button-container">
                    <Link to="/" className="home-button">Home</Link>
                </div>
                {isLoggedIn ? (
                    isAdmin ? (
                        <>
                            <h2>Create Device</h2>
                            <div className="create-device-form">
                                <div>
                                    <label>Name:</label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={newDevice.name}
                                        onChange={(event) => this.handleInputChange(event, 'name')}
                                    />
                                </div>
                                <div>
                                    <label>Description:</label>
                                    <input
                                        type="text"
                                        name="description"
                                        value={newDevice.description}
                                        onChange={(event) => this.handleInputChange(event, 'description')}
                                    />
                                </div>
                                <div>
                                    <label>Address:</label>
                                    <input
                                        type="text"
                                        name="address"
                                        value={newDevice.address}
                                        onChange={(event) => this.handleInputChange(event, 'address')}
                                    />
                                </div>
                                <div>
                                    <label>Maximum Energy Consumption:</label>
                                    <input
                                        type="text"
                                        name="maximumEnergyConsumption"
                                        value={newDevice.maximumEnergyConsumption}
                                        onChange={(event) => this.handleInputChange(event, 'maximumEnergyConsumption')}
                                    />
                                </div>
                                <div>
                                    <label>User:</label>
                                    <select
                                        name="userId"
                                        value={newDevice.userId}
                                        onChange={(event) => this.handleInputChange(event, 'userId')}
                                    >
                                        <option value="">Select User</option>
                                        {users.map((user) => (
                                            <option key={user.id} value={user.id}>
                                                {user.username}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <button className="action-button" onClick={this.handleCreateDevice}>Create Device</button>
                            </div>

                            <h2>List of Devices</h2>
                            <div className="user-list">
                                {this.state.devices.map((device) => (
                                    <div key={device.id} className="user-box">
                                        {editingDeviceId === device.id ? (
                                            <div>
                                                <label>Name:</label>
                                                <input
                                                    type="text"
                                                    name="name"
                                                    value={editedDevice.name}
                                                    onChange={(event) => this.handleEditInputChange(event, 'name')}
                                                />
                                                <label>Description:</label>
                                                <input
                                                    type="text"
                                                    name="description"
                                                    value={editedDevice.description}
                                                    onChange={(event) => this.handleEditInputChange(event, 'description')}
                                                />
                                                <label>Address:</label>
                                                <input
                                                    type="text"
                                                    name="address"
                                                    value={editedDevice.address}
                                                    onChange={(event) => this.handleEditInputChange(event, 'address')}
                                                />
                                                <label>Maximum Energy Consumption:</label>
                                                <input
                                                    type="text"
                                                    name="maximumEnergyConsumption"
                                                    value={editedDevice.maximumEnergyConsumption}
                                                    onChange={(event) => this.handleEditInputChange(event, 'maximumEnergyConsumption')}
                                                />
                                                <div>
                                                    <label>User:</label>
                                                    <select
                                                        name="userId"
                                                        value={editedDevice.userId}
                                                        onChange={(event) => this.handleEditInputChange(event, 'userId')}
                                                    >
                                                        <option value="">Select User</option>
                                                        {users.map((user) => (
                                                            <option key={user.id} value={user.id}>
                                                                {user.username}
                                                            </option>
                                                        ))}
                                                    </select>
                                                </div>
                                                <button className="action-button" onClick={this.handleSaveDevice}>Save</button>
                                            </div>
                                        ) : (
                                            <div>
                                                <h3>{device.name}</h3>
                                                <p><strong>Description:</strong> {device.description}</p>
                                                <p><strong>Address:</strong> {device.address}</p>
                                                <p><strong>Maximum Energy Consumption:</strong> {device.maximumEnergyConsumption}</p>
                                                {device.user ? <p><strong>User:</strong> {device.user.username}</p> : <p><strong>User:</strong> No user assigned</p>}
                                                <button className="action-button" onClick={() => this.handleEditDevice(device)}>Edit</button>
                                                <button className="action-button" onClick={() => this.handleDeleteDevice(device.id)}>Delete</button>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                            <button onClick={this.handleLogout}>Logout</button>
                        </>
                    ) : (
                        <div className="error-message">
                            <p>You can't access this page.</p>
                        </div>
                    )
                ) : (
                    <div className="error-message">
                        <p>You are not logged in.</p>
                    </div>
                )}
                {errorMessage && (
                    <div className="error-message">
                        <p>{errorMessage}</p>
                    </div>
                )}
            </div>
        );
    }
}

export default DeviceListPage;