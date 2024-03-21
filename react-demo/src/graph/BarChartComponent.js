/*
// BarChartComponent.js
import React, { useEffect, useRef } from 'react';
import { Chart } from './chartConfig'; // Adjust the path based on your project structure

const BarChartComponent = ({ data }) => {
    const chartRef = useRef(null);

    useEffect(() => {
        // Destroy the existing chart if it exists
        if (chartRef.current) {
            chartRef.current.destroy();
        }

        // Create a new chart
        const ctx = document.getElementById('barChart').getContext('2d');
        chartRef.current = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(entry => entry.hour), // Assuming there's an 'hour' property in your data
                datasets: [
                    {
                        label: 'Energy Consumption',
                        data: data.map(entry => entry.energy), // Assuming there's an 'energy' property in your data
                    },
                ],
            },
            options: {
                // Your chart options here
            },
        });

        // Cleanup on component unmount
        return () => {
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        };
    }, [data]);

    return <canvas id="barChart" width="400" height="200"></canvas>;
};

export default BarChartComponent;
*/
