import React, { Component } from 'react';

class UserInsertionModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
        };
    }

    handleInputChange = (event) => {
        const { name, value } = event.target;
        this.setState({ [name]: value });
    };

    handleInsertUser = () => {
        // Prepare the user data
        const userData = {
            username: this.state.username,
            password: this.state.password,
            role: "user",
        };

        // Send a POST request to create a new user
        fetch('http://localhost:8080/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        })
            .then((response) => {
                if (response.status === 201) {
                    // User created successfully
                    this.props.onUserInserted();
                }
            })
            .catch((error) => {
                console.error('Error creating user:', error);
            });
    };

    render() {
        return (
            <div className="modal">
                <div className="modal-content">
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
                            type="password"
                            name="password"
                            value={this.state.password}
                            onChange={this.handleInputChange}
                        />
                    </div>
                    <button onClick={this.handleInsertUser}>Insert User</button>
                </div>
            </div>
        );
    }
}

export default UserInsertionModal;
