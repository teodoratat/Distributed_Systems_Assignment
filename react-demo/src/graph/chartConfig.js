

// Register the date-fns adapter
// chartConfig.js
import { Chart, registerables } from 'chart.js/auto';
import 'chartjs-adapter-date-fns';

// Register the necessary modules
Chart.register(...registerables);

export { Chart };

