import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import axios from 'axios';
import { Line } from 'react-chartjs-2';
import {da} from "date-fns/locale";

const EnergyConsumptionPage = () => {
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [energyData, setEnergyData] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchData = async () => {
        setLoading(true);
        try {
            const deviceId = sessionStorage.getItem('selectedDeviceId');
            const response = await axios.get(
                `http://localhost:8081/hourly_energy_consumption/devicess/${deviceId}/${selectedDate.toISOString().split('T')[0]}`
            );
            console.log(response.data);
            setEnergyData(response.data);
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleDateChange = (date) => {
        setSelectedDate(date);
    };

    const generateGraphData = () => {
        const labels = [];
        const data = [];

        energyData.forEach((hourlyData) => {
            const bucharestHour = new Date(hourlyData.date).toLocaleTimeString('en-US', {
                hour: '2-digit',
                minute: '2-digit',
                timeZone: 'Europe/Bucharest', // Adjust the time zone as needed
            });

            labels.push(bucharestHour);
            data.push(hourlyData.totalConsumption);
        });

        return {
            labels,
            datasets: [
                {
                    label: 'Hourly Energy Consumption',
                    data,
                    fill: false,
                    borderColor: 'rgba(75,192,192,1)',
                    lineTension: 0.1,
                },
            ],
        };
    };

    return (
        <div>
            <h1>Energy Consumption Page</h1>
            <DatePicker selected={selectedDate} onChange={handleDateChange} />
            <button onClick={fetchData}>Fetch Data</button>
            {loading && <p>Loading...</p>}
            {energyData.length > 0 ? (
                <div>
                    <h2>Hourly Energy Consumption for {selectedDate.toISOString().split('T')[0]}</h2>
                    <Line
                        data={generateGraphData()}
                        options={{
                            scales: {
                                x: {
                                    type: 'category',
                                },
                                y: {
                                    beginAtZero: true,
                                },
                            },
                        }}
                    />
                </div>
            ) : (
                <p>No measures from this day</p>
            )}
        </div>
    );
};

export default EnergyConsumptionPage;
