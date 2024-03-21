import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import NavigationBar from './navigation-bar';
import Home from './home/home';
import PersonContainer from './person/person-container';
import ErrorPage from './commons/errorhandling/error-page';
import LoginPage from './login/login';
import styles from './commons/styles/project-style.css';
import UserDetailPage from './user_details/user_details';
import AdminUserCreationPage from "./crud_user/user-create";
import DeviceListPage from "./crud_user/crud-devices";
import HistoricalDataPage from "./graph/Energy";
import Energy from "./graph/Energy";
import EnergyConsumptionPage from "./graph/Energy";
import WebSocketComponent from './user_details/WebSocket';
import ChatRoom from "./wesockets/Chat";
import ChatRoomPage from "./wesockets/ChatRoomPage"; // Import WebSocketComponent

class App extends React.Component {
    render() {
        return (
            <div className={styles.back}>
                <Router>
                    <div>
                        <NavigationBar />
                        <WebSocketComponent /> {WebSocketComponent}

                        <Switch>
                            <Route exact path='/' render={() => <Home />} />
                            <Route exact path='/login' component={LoginPage} />
                            <Route exact path='/user_details' component={UserDetailPage} />
                            <Route path="/user-create" component={AdminUserCreationPage} />
                            <Route exact path='/error' render={() => <ErrorPage />} />
                            <Route exact path='/crud-device' component={DeviceListPage} />
                            <Route path="/historical-data" component={EnergyConsumptionPage} />
                            <Route path="/websocket" component={WebSocketComponent} /> {/* Route for WebSocketComponent */}
                            <Route path="/chat" component={ChatRoomPage} />
                            <Route render={() => <ErrorPage />} />
                        </Switch>
                    </div>
                </Router>
            </div>
        );
    }
}

export default App;
