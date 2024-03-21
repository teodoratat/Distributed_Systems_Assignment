import React, { Component } from 'react';
import './login-style.css';
import { withRouter } from 'react-router-dom';
import Cookies from 'js-cookie';

class LoginPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loginUsername: '',
            loginPassword: '',
            loginError: null, // To store any login error messages
        };
    }

    componentDidMount() {
        // Check if user is already logged in
        const userId = sessionStorage.getItem('userId');
        if (userId) {
            // User is logged in, redirect to home page
            this.props.history.push('/');
        }
    }

    handleInputChange = (event) => {
        const { name, value } = event.target;
        this.setState({ [name]: value });
    };

    handleLogin = () => {
        const { loginUsername, loginPassword } = this.state;

        // Send a POST request to the signin API
        fetch(`http://localhost:8080/api/auth/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: loginUsername,
                password: loginPassword,
            }), credentials: 'include', // Include cookies
        })
            .then((response) => {
                if (response.status === 200) {
                    console.log("im loggedd in")
                    return response.json();
                } else {
                    // Handle login error
                    this.setState({ loginError: 'Invalid username or password' });
                    throw new Error('Invalid username or password');
                }
            })
            .then((data) => {
                console.log('Logged in user:', data);


                // Store user details in sessionStorage
                sessionStorage.setItem('userId', data.id);
                sessionStorage.setItem('username', data.username);
                sessionStorage.setItem('role', data.roles.join(',')); // Convert roles array to string
                sessionStorage.setItem('jwt',data.jwt);
                console.log(data.jwtToken);
                // Set JWT cookie using js-cookie
                //Cookies.set('bezkoder', data.bezkoder, { expires: 1 }); // Expires in 1 day
                //console.log('Cookie set:', Cookies.get('bezkoder')); // Log the cookie here
                //const userDataFromCookie = Cookies.get('bezkoder');
                //console.log("cookie bun"+userDataFromCookie)
                // Redirect based on user's role
                if (data.roles.includes('user')) {
                    console.log('Redirecting to /user_details/user_details');
                    this.props.history.push('/user_details');
                } else if (data.roles.includes('admin')) {
                    console.log('Redirecting to /user-create');
                    this.props.history.push('/user-create');
                }
            })
            .catch((error) => {
                console.error('Login error:', error);
                // Handle network error
            });
    };


    render() {
        // Check if user is not logged in
        const userId = sessionStorage.getItem('userId');
        if (!userId) {
            return (
                <div className="container">
                    <h2>Login</h2>
                    <form>
                        <div>
                            <label>Username:</label>
                            <input
                                type="text"
                                name="loginUsername"
                                value={this.state.loginUsername}
                                onChange={this.handleInputChange}
                            />
                        </div>
                        <div>
                            <label>Password:</label>
                            <input
                                type="password"
                                name="loginPassword"
                                value={this.state.loginPassword}
                                onChange={this.handleInputChange}
                            />
                        </div>
                        <div>
                            <button type="button" onClick={this.handleLogin}>
                                Login
                            </button>
                        </div>
                    </form>
                    {this.state.loginError && <p className="error-message">{this.state.loginError}</p>}
                </div>
            );
        }

        return <p>You are already logged in. Redirecting to home page...</p>;
    }
}

export default withRouter(LoginPage);
