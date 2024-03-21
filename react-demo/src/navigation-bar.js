import React from 'react';
import logo from './commons/images/logo.jpg';
import {
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    Nav,
    Navbar,
    NavbarBrand,
    NavLink,
    UncontrolledDropdown,
} from 'reactstrap';

const textStyle = {
    color: 'white',
    textDecoration: 'none',
};

const NavigationBar = () => {
    // Check the user's role from session storage
    const userRole = sessionStorage.getItem('role');

    // Create an array of menu items based on the user's role
    let menuItems = [];
    if (userRole === 'admin') {
        menuItems = [
            { label: 'Users', link: '/user-create' },
            { label: 'Smart Devices', link: '/crud-device' },
        ];
    } else if (userRole === 'user') {
        menuItems = [
            { label: 'Account', link: '/user_details/user_details' },
        ];
    }

    return (
        <div>
            <Navbar color="black" light expand="md">
                <NavbarBrand href="/">
                    <img src={logo} width="50" height="35" />
                </NavbarBrand>
                <Nav className="mr-auto" navbar>
                    {userRole ? ( // If userRole is not empty
                        <UncontrolledDropdown nav inNavbar>
                            <DropdownToggle style={textStyle} nav caret>
                                Menu
                            </DropdownToggle>
                            <DropdownMenu right>
                                {menuItems.map((item, index) => (
                                    <DropdownItem key={index}>
                                        <NavLink to={item.link} style={{ ...textStyle, color: 'black' }}>
                                            {item.label}
                                        </NavLink>
                                    </DropdownItem>
                                ))}
                            </DropdownMenu>
                        </UncontrolledDropdown>
                    ) : (
                        <NavLink to="/login" style={textStyle}>
                            Join Us!
                        </NavLink>
                    )}
                </Nav>
            </Navbar>
        </div>
    );
};

export default NavigationBar;
