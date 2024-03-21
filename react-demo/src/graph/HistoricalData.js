/*
// HistoricalDataPage.js
import React, { useState, useEffect } from 'react';
import { withRouter } from 'react-router-dom';
import CalendarComponent from './CalendarComponent';
import LineChartComponent from './LineChartComponent';
import BarChartComponent from './BarChartComponent';
import { Chart } from './chartConfig'; // Adjust the path based on your project structure

const HistoricalDataPage = ({ match }) => {
    const deviceId = sessionStorage.getItem('selectedDeviceId');
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
    const [energyData, setEnergyData] = useState([]);

    const handleDateChange = (date) => {
        setSelectedDate(date);
        fetchEnergyData(deviceId, date);
    };

    useEffect(() => {
        fetchEnergyData(deviceId, selectedDate);
    }, [deviceId, selectedDate]);

    const fetchEnergyData = async (deviceId, date) => {
        try {
            // Make an API request to fetch historical energy data
            const response = await fetch(`http://localhost:8081/hourly_energy_consumption/devicess/${deviceId}/${date}`);

            if (response.ok) {
                const data = await response.json();
                console.log(data);
                // Ensure data is an array before setting the state
                if (Array.isArray(data)) {
                    setEnergyData(data);
                } else {
                    console.error('Error: Data is not an array');
                }
            } else {
                console.error('Error fetching energy data:', response.status);
            }
        } catch (error) {
            console.error('Error fetching energy data:', error);
        }
    };


    // Helper function to format the fetched data for the chart
    const formatChartData = (data) => {
        // Check if the data array is not empty
        if (data && data.length > 0) {
            return {
                labels: data.map(entry => entry.hour),
                datasets: [
                    {
                        label: 'Energy Consumption',
                        data: data.map(entry => entry.energy),
                        fill: false,
                        borderColor: 'rgba(75,192,192,1)',
                        tension: 0.1,
                    },
                ],
            };
        } else {
            // Handle the case when the data array is empty
            console.error('Error: Data array is empty');
            return null; // or return a default chart configuration
        }
    };



    return (
        <div>
            <h2>Historical Energy Consumption for Device {deviceId}</h2>
            <CalendarComponent onDateChange={handleDateChange} />
            {energyData && energyData.length > 0 && (
                <>
                    <LineChartComponent data={energyData} />
                    <BarChartComponent data={energyData} />
                </>
            )}
        </div>
    );

};

export default withRouter(HistoricalDataPage);
*/
