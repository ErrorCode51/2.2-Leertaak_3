//this is the data fetcher for the weather history table

$(document).ready(function() {
    function readTextFile(file, row) {
        const rawFile = new XMLHttpRequest();
        rawFile.open("GET", file, false);
        rawFile.onreadystatechange = function ()
        {
            if (!(rawFile.readyState === 4 && (rawFile.status === 200 || rawFile.status === 0))) {
                return;
            }
            const allText = rawFile.responseText;
            const data_needed = allText.substring(70, 139);
            const array_data_needed = data_needed.split(',');
            console.log(array_data_needed[1]);
            $('#' + row).append('<td>' + array_data_needed[2] + ' °C &nbsp; &nbsp; </td>');
            $('#' + row).append('<td>' + array_data_needed[3] + ' °C</td>');
            $('#' + row).append('<td>' + array_data_needed[7] + ' km/h</td>');
            $('#' + row).append('<td>' + array_data_needed[8] + ' cm</td>');
            $('#' + row).append('<td>&nbsp;&nbsp;' + array_data_needed[9] + ' cm</td>');
            $('#' + row).append('<td>' + array_data_needed[12] + '%</td>');
        };
        rawFile.send(null);
    }

    //the choices submitted by the HTML form
    const current_date = $('.current-date').text();
    const current_date_split = current_date.split('-');
    const current_location = $('.current-location').text();

    //select the right file
    for (let i = 0; i < 10; i++) {
        readTextFile("/DB/_database_/" + current_date_split[0] + "/" + current_date_split[1] + "/" + current_date_split[2] + "/" + current_location + "/" + current_date + "_h0" + i +".csv", i);
    }
    for (let i = 10; i < 24; i++) {
        readTextFile("/DB/_database_/" + current_date_split[0] + "/" + current_date_split[1] + "/" + current_date_split[2] + "/" + current_location + "/" + current_date + "_h" + i +".csv", i);
    }

});
