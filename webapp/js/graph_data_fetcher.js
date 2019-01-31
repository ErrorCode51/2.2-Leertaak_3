$(document).ready(function() {
    let updatingValue = 0;

    function readTextFile(file) {
        const rawFile = new XMLHttpRequest();
        rawFile.open("GET", file, false);
        rawFile.onreadystatechange = function ()
        {
            if (!(rawFile.readyState === 4 && (rawFile.status === 200 || rawFile.status === 0))) {
                return;
            }
            const allText = rawFile.responseText;
            const data_needed = allText.substring(allText.length - 67, allText.length);
            const array_data_needed = data_needed.split(',');

            updatingValue = parseFloat(array_data_needed[7]);
        };
        rawFile.send(null);
    }



    const t_date = $('.tdate').text();
    const t_date_split = t_date.split('-');
    const time = $('.time').text();
    const current_location = $('.current-location').text();
    const city = $('.city').text();


    //Todo: change path when connected to database
    function startReading() {
        let fileName = "http://localhost/2.2-Leertaak_3/webapp/_database_/" + t_date_split[0] + "/" + t_date_split[1] + "/" + t_date_split[2] + "/" + current_location + "/" + t_date + "_h01" +".csv";
        let realFile = fileName.replace(/ /g, '');

        readTextFile(realFile);
        setTimeout(startReading, 1000);
    }
    startReading();



    Highcharts.chart('container', {
        chart: {
            type: 'spline',
            animation: Highcharts.svg, // don't animate in old IE
            events: {
                load: function () {

                    // set up the updating of the chart every minute
                    let series = this.series[0];
                    setInterval(function () {
                        let x = (new Date()).getTime(); // current time
                        series.addPoint([x, updatingValue], true, true);
                    }, 1000);
                }
            }
        },

        time: {
            useUTC: false
        },

        title: {
            text: "Rainfall in " + city
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Precipitation in cm'
            },
            plotLines: [{
                value: 100,
                width: 100,
                color: '#2e3192'
            }]
        },
        tooltip: {
            headerFormat: '<b>{series.name}</b><br/>',
            pointFormat: '{point.x:%Y-%m-%d %H:%M:%S}<br/>{point.y:.2f}'
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
            name: 'Rainfall',
            data: (function () {
                // generate an array of random data
                let data = [],
                    time = (new Date()).getTime(),
                    i;

                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: updatingValue
                    });
                }
                return data;
            }())
        }]
    });
});