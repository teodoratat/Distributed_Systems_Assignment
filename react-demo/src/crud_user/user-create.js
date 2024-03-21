import React, { Component } from 'react';
import '../commons/styles/user_details.css';
import {Link} from "react-router-dom";
import './user-create.css';
import Cookies from 'js-cookie';
class AdminUserCreationPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '', // Add password field for create user
            role: 'user', // Initialize role to 'User'
            userList: [],
            editUserId: null,
            editedUsername: '',
            editedRole: '',
            sessionEmpty: false,
            isLoggedIn: false,
            isAdmin: false,
        };
    }

    handleLogout = async () => {
        try {
            // Clear session storage
            sessionStorage.clear();

            // Send POST request to logout endpoint
            const response = await fetch('http://localhost:8080/api/auth/signout', {
                method: 'POST',
                credentials: 'include', // Send cookies along with the request
            });

            if (response.ok) {
                // Successfully logged out, redirect to the home page
                this.props.history.push('/');
            } else {
                // Handle error response
                console.error('Logout failed:', response.statusText);
                // You may want to display an error message to the user
            }
        } catch (error) {
            console.error('Logout failed:', error.message);
            // Handle network error
            // You may want to display an error message to the user
        }
    };

    /*componentDidMount() {
        // Fetch the list of users when the component mounts
        this.fetchUserList();
    }*/
    componentDidMount() {
        const userId = sessionStorage.getItem('userId');

        if (userId) {
            this.fetchUserList();
            if (sessionStorage.getItem('role') === 'admin') {
                this.setState({isLoggedIn: true, isAdmin: true}); // Set isLoggedIn and isAdmin to true
            } else {
                this.setState({isLoggedIn: true});
            }
        } else {
            //alert.show('User ID not found in session storage');
            console.error('User ID not found in session storage');
            this.setState({isLoggedIn: false});
        }
    }


    handleInputChange = (event) => {
        const {name, value} = event.target;
        this.setState({[name]: value});
    };
    handleDeleteUser = (userId) => {
        // Retrieve JWT token from session storage
        const jwt = sessionStorage.getItem('jwt');
        console.log('cookie in delete '+jwt)
        // Check if JWT token is available
        if (!jwt) {
            console.error('JWT token not found in session storage');
            return;
        }

        // Send a DELETE request to delete the user
        fetch(`http://localhost:8080/api/user/delete/${userId}`, {
            method: 'DELETE',
            credentials: 'include', // Include this line to send cookies
            headers: {
                'Authorization': `Bearer ${jwt}`, // Include your JWT token
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    // Deletion was successful
                    this.fetchUserList(); // Refresh the user list after deletion
                } else if (response.status === 404) {
                    // User not found, handle accordingly (e.g., show an error message)
                    console.error('User not found');
                } else {
                    // Handle other errors (e.g., network issues)
                    console.error('Error deleting user. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error deleting user:', error);
            });
    };


    handleCreateUser = () => {
        // Prepare the user data
        const { username, password, role } = this.state;

        const userData = {
            username,
            password,
            role: role, // Wrap the role in an array as expected by the backend
        };

        console.log("create user:"+userData.username+userData.password+userData.roles);
        // Send a POST request to create a new user
        fetch('http://localhost:8080/api/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        })
            .then((response) => {
                if (response.status === 200) {
                    // User created successfully
                    this.setState({
                        username: '',
                        password: '',
                        role: 'user', // Reset role to 'user'
                    });
                    this.fetchUserList(); // Refresh the user list after creation
                } else {
                    console.error('Error creating user. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error creating user:', error);
            });
    };



    fetchUserList = () => {
        const userId = sessionStorage.getItem('userId');
        const jwtToken = sessionStorage.getItem('jwt')

        //const jwtToken = Cookies.get('bezkoder'); // Replace 'bezkoder' with your actual cookie name
       // console.log("cookie in get "+ jwtToken);
        // Fetch the list of users based on the user ID
        fetch(`http://localhost:8080/api/user/allUsers/${userId}`, {
            method: 'GET',
            credentials: 'include', // Include this line to send cookies
            headers: {
                'Authorization': `Bearer ${jwtToken}`, // Include your JWT token if needed
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    response.json().then((data) => {
                        this.setState({ userList: data });
                    });
                }
            })
            .catch((error) => {
                console.error('Error fetching user list:', error);
            });
    };

    handleEditUser = (userId) => {
        // Set the edit mode for the specified user
        this.setState({
            editUserId: userId,
            editedUsername: this.state.userList.find((user) => user.id === userId)?.username || '',
            editedRole: this.state.userList.find((user) => user.id === userId)?.role || '',
        });
    };

    handleSaveEdit = () => {
        const { editUserId, editedUsername, editedRole } = this.state;
        const originalUser = this.state.userList.find((user) => user.id === editUserId);

        // Create a new user object with updated fields
        const updatedUser = {
            id: editUserId,
            username: editedUsername,
            password: originalUser.password, // Retain the original password
            role: editedRole,
        };

        const jwtToken = sessionStorage.getItem('jwt')
        // Send a PUT request to update the user
        fetch(`http://localhost:8080/api/user/update/${editUserId}`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Authorization': `Bearer ${jwtToken}`,
                'Content-Type': 'application/json',
            },


            body: JSON.stringify(updatedUser),
        })
            .then((response) => {
                if (response.status === 200) {
                    // User updated successfully
                    this.fetchUserList();
                    this.setState({
                        editUserId: null,
                        editedUsername: '',
                        editedRole: '',
                    });
                } else {
                    console.error('Error updating user. Server returned status:', response.status);
                }
            })
            .catch((error) => {
                console.error('Error updating user:', error);
            });
    };


    render() {
        const {isLoggedIn, isAdmin} = this.state;

        return (
            <div className="admin-user-creation">
                <div className="navigation">
                    <Link to="/crud-device" className="crud-devices-button">
                        Go to Device
                    </Link>
                    {/* Add a new Link to go to the chat page */}
                    <Link to="/chat" className="button-link chat-button">
                        Chat with users
                    </Link>
                </div>
                <div className="button-container">
                    <Link to="/" className="home-button">
                        Home
                    </Link>
                </div>

                {isLoggedIn ? (
                    isAdmin ? (
                        <div>
                            <h2>Create User</h2>
                            <div>
                                <label>Username:</label>
                                <input
                                    type="text"
                                    name="username"
                                    value={this.state.username}
                                    onChange={this.handleInputChange}
                                />
                            </div>
                            <div>
                                <label>Password:</label>
                                <input
                                    type="text"
                                    name="password"
                                    value={this.state.password}
                                    onChange={this.handleInputChange}
                                />
                            </div>
                            <div>
                                <label>Role:</label>
                                <select
                                    name="role"
                                    value={this.state.role}
                                    onChange={this.handleInputChange}
                                >
                                    <option value="user">user</option>
                                    {isAdmin && <option value="admin">admin</option>}
                                </select>
                            </div>
                            <button onClick={this.handleCreateUser} className="create-button">
                                Create User
                            </button>

                            <h2>List of Users</h2>
                            <div className="user-list">
                                {this.state.userList.map((user) => (
                                    <div key={user.id} className="user-box">
                                        <p>User ID: {user.id}</p>
                                        {this.state.editUserId === user.id ? (
                                            <div>
                                                <label>Edited Username:</label>
                                                <input
                                                    type="text"
                                                    name="editedUsername"
                                                    value={this.state.editedUsername}
                                                    onChange={this.handleInputChange}
                                                />
                                                <div>
                                                    <label>Edited Role:</label>
                                                    <select
                                                        name="editedRole"
                                                        value={this.state.editedRole}
                                                        onChange={this.handleInputChange}
                                                    >
                                                        <option value="user">user</option>
                                                        <option value="admin">admin</option>
                                                    </select>
                                                </div>

                                                <button onClick={this.handleSaveEdit}>Save</button>
                                            </div>
                                        ) : (
                                            <div>
                                                <p>Username: {user.username}</p>
                                                <p>Role: {user.role}</p>
                                            </div>
                                        )}
                                        <button onClick={() => this.handleEditUser(user.id)}>Edit</button>
                                        <button onClick={() => this.handleDeleteUser(user.id)}>Delete</button>
                                    </div>
                                ))}
                            </div>
                            <button onClick={this.handleLogout}>Logout</button>
                        </div>
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
            </div>
        );
    }
}

export default AdminUserCreationPage;
