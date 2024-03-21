import React from 'react';
import { Button, Container, Jumbotron } from 'reactstrap';
import { Link } from 'react-router-dom';

import BackgroundImg from '../commons/images/energynice.png';

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'repeat',
    width: '100%',
    // height: '1920px',
    // backgroundColor: #f0f8ff,
    backgroundImage: `url(${BackgroundImg})`,
};

const jumbotronStyle = {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    padding: '20px',
    borderRadius: '10px',
};

const textStyle = {
    color: 'white',
};

const buttonContainerStyle = {
    marginTop: '20px',
};

class Home extends React.Component {
    render() {
        // Check the user's role from session storage
        const userRole = sessionStorage.getItem('role');
        let content;

        if (userRole === 'admin') {
            // Admin user, show buttons to redirect to CRUD pages
            content = (
                <div style={buttonContainerStyle}>
                    <Link to="/user-create">
                        <Button color="primary" className="mr-3">Manage Users</Button>
                    </Link>
                    <Link to="/crud-device">
                        <Button color="primary">Manage Smart Devices</Button>
                    </Link>
                </div>
            );
        } else if (userRole === 'user') {
            // Regular user, show a button to redirect to the user details page
            content = (
                <Link to="/user_details">
                    <Button color="primary">User Details</Button>
                </Link>
            );
        } else {
            // Session storage is empty or undefined, show the "Join Us" button
            content = (
                <Link to="/login">
                    <Button color="primary">Join us!</Button>
                </Link>
            );
        }

        return (
            <div>
                <Jumbotron fluid style={backgroundStyle}>
                    <Container fluid>
                        <div style={jumbotronStyle}>
                            <h1 className="display-3" style={textStyle}>
                                Energy Management System
                            </h1>
                            <p className="lead" style={textStyle}>
                                <b>Designed to manage users and their associated smart energy metering devices.</b>
                            </p>
                            <hr className="my-2" />
                            <p style={textStyle}>
                                <b>
                                    Our system empowers you to efficiently monitor and manage your energy consumption,
                                    harnessing the capabilities of smart energy metering devices.
                                </b>
                            </p>
                            {content}
                        </div>
                    </Container>
                </Jumbotron>
            </div>
        );
    }
}

export default Home;
