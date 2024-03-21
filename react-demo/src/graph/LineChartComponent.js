/*
// LineChartComponent.js
import React, { useEffect, useRef } from 'react';
import { Chart } from 'chart.js/auto';

const LineChartComponent = ({ data }) => {
    const chartRef = useRef(null);

    useEffect(() => {
        // Destroy the existing chart if it exists
        if (chartRef.current) {
            chartRef.current.destroy();
        }

        // Create a new chart
        const ctx = document.getElementById('lineChart').getContext('2d');
        chartRef.current = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.labels,
                datasets: [
                    {
                        label: 'Energy Consumption',
                        data: data.datasets[0].data, // Assuming your data structure
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 2,
                        fill: false,
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        type: 'time', // Assuming your x-axis represents time
                        time: {
                            unit: 'hour', // You can adjust the time unit as needed
                        },
                        title: {
                            display: true,
                            text: 'Time',
                        },
                    },
                    y: {
                        type: 'linear', // Assuming your y-axis is linear
                        title: {
                            display: true,
                            text: 'Energy Consumption',
                        },
                    },
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Line Chart Title',
                    },
                },
            },
        });

        // Cleanup on component unmount
        return () => {
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        };
    }, [data]);

    return <canvas id="lineChart" width="400" height="200"></canvas>;
};

export default LineChartComponent;
*/
