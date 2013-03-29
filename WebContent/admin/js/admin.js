

/* ---------------------------------------------------------------------- */
/*	On Page Load
/* ---------------------------------------------------------------------- */

$(document).ready( function() {   
	$.ajax({
        type : "get",
        url : "offers/analytics",
        success : function(responseJson) {        	
        	display_charts(responseJson);
        }
    }); 
});

function display_charts(responseJson)
{
	display_monthly_chart(responseJson.monthByWeeks);
	
	display_weekly_chart(responseJson.weekByDays, responseJson.latestWeek);
	
	display_businesses_pie_chart(responseJson.businesses);
}

function display_monthly_chart(month) 
{	
	if ($("#monthly-bar-chart").length) {
		var arrayLength = month.length;
		
		var offerData = [];
		var offerRecordData = [];
		var redeemRecordData = [];
		var minval = null;
		var maxval = null;
		var xAxisTicks = [];
		
		for (var i = 0; i < arrayLength; i += 1)
		{
			var currentWeekValues = month[i];
			var currentWeek = new Date(currentWeekValues.week);
			
			if (minval == null || currentWeek < minval)
			{
				minval = currentWeek;
			}
			
			if (maxval == null || currentWeek > maxval)
			{
				maxval = currentWeek;
			}

			xAxisTicks.push(currentWeek.getTime());
			offerData.push([currentWeek.getTime(), currentWeekValues.Offers]);
			offerRecordData.push([currentWeek.getTime(), currentWeekValues.OfferRecords]);
			redeemRecordData.push([currentWeek.getTime(), currentWeekValues.RedeemRecords]);
		}
		
		var individualWidth = 20 * 60 * 60 * 1000;
		var ds = new Array();
		ds.push({
			label : "texts",
			data : offerData,
			bars : {
				show : true,
				barWidth : individualWidth,
				order : 1,
			}
		});
		ds.push({
			label : "offer views",
			data : offerRecordData,
			bars : {
				show : true,
				barWidth : individualWidth,
				order : 2
			}
		});
		ds.push({
			label : "redeem views",
			data : redeemRecordData,
			bars : {
				show : true,
				barWidth : individualWidth,
				order : 3
			}
		});
		
		minval.setDate(minval.getDate()-7);
		maxval.setDate(maxval.getDate()+7);
		
		var options = {
				xaxis : {
					mode : "time",
					ticks: xAxisTicks,
				    timeformat: "%m/%d",
				    min: minval,
				    max: maxval
				},
				colors : [$chrt_second, $chrt_fourth, "#666", "#BBB"],
				grid : {
					show : true,
					hoverable : true,
					clickable : true,
					tickColor : $chrt_border_color,
					borderWidth : 0,
					borderColor : $chrt_border_color,
				},
				legend : {
					show : true,
					noColumns : 1, // number of colums in legend table
					labelFormatter : null, // fn: string -> string
					labelBoxBorderColor : "#000", // border color for the little label boxes
					container : null, // container (as jQuery object) to put legend in, null means default on top of graph
					position : "ne", // position of default legend container within plot
					margin : [5, 10], // distance from grid edge to default legend container within plot
					backgroundColor : "#efefef", // null means auto-detect
					backgroundOpacity : 1 // set to 0 to avoid background
				},
				tooltip : true,
				tooltipOpts : {
					content : "<b>%x</b> = <span>%y</span>",
					defaultTheme : false
				}

			};

		//Display graph
		$.plot($("#monthly-bar-chart"), ds, options);
	};
};

function display_weekly_chart(latestWeekValues, latestWeek) 
{	
	if ($("#weekly-bar-chart").length) {
		
		// Store the start of the week
		var minval = new Date(latestWeek);
		
		var offerData = [];
		var offerRecordData = [];	
		var redeemRecordData = [];
		var xAxisTicks = [];
		
		var currentDate = new Date(latestWeek);
		for (var i = 0; i < 7; i += 1)
		{
			if (latestWeekValues[i].Offers > 0)
			{
				offerData.push([currentDate.getTime(), latestWeekValues[i].Offers]);
			}
			
			if (latestWeekValues[i].OfferRecords > 0)
			{
				offerRecordData.push([currentDate.getTime(), latestWeekValues[i].OfferRecords]);
			}
			
			if (latestWeekValues[i].RedeemRecords > 0)
			{
				redeemRecordData.push([currentDate.getTime(), latestWeekValues[i].RedeemRecords]);
			}
			
			xAxisTicks.push(currentDate.getTime());
	
			currentDate.setDate(currentDate.getDate()+1);
		}
		
		var individualWidth = 5 * 60 * 60 * 1000;
		var ds = new Array();
		if (offerData.length > 0)
		{
			ds.push({
				label : "texts",
				data : offerData,
				bars : {
					show : true,
					barWidth : individualWidth,
					order : 1,
				}
			});	
		};
		if (offerRecordData.length > 0)
		{
			ds.push({
				label : "offer views",
				data : offerRecordData,
				bars : {
					show : true,
					barWidth : individualWidth,
					order : 2
				}
			});	
		};
		if (redeemRecordData.length > 0)
		{
			ds.push({
				label : "redeem views",
				data : redeemRecordData,
				bars : {
					show : true,
					barWidth : individualWidth,
					order : 3
				}
			});	
		};
		
		var maxval = new Date(minval.getTime());
		minval.setDate(minval.getDate()-1);
		maxval.setDate(maxval.getDate()+7);
		
		var options = {
				xaxis : {
					mode : "time",
					ticks: xAxisTicks,
					timeformat: "%m/%d",
				    min : minval,
				    max : maxval
				},
				colors : [$chrt_second, $chrt_fourth, "#666", "#BBB"],
				grid : {
					show : true,
					hoverable : true,
					clickable : true,
					tickColor : $chrt_border_color,
					borderWidth : 0,
					borderColor : $chrt_border_color,
				},
				legend : {
					show : true,
					noColumns : 1, // number of colums in legend table
					labelFormatter : null, // fn: string -> string
					labelBoxBorderColor : "#000", // border color for the little label boxes
					container : null, // container (as jQuery object) to put legend in, null means default on top of graph
					position : "ne", // position of default legend container within plot
					margin : [5, 10], // distance from grid edge to default legend container within plot
					backgroundColor : "#efefef", // null means auto-detect
					backgroundOpacity : 1 // set to 0 to avoid background
				},
				tooltip : true,
				tooltipOpts : {
					content : "<b>%x</b> = <span>%y</span>",
					defaultTheme : false
				}

			};

		//Display graph
		$.plot($("#weekly-bar-chart"), ds, options);
	};
};

function display_businesses_pie_chart(businesses)
{	
	/* pie chart */	
	if ($('#businesses-pie-chart').length) {

		var data_pie = [];
		var index = 0;
		for (var bizcodename in businesses)
		{
			data_pie[index] = {
				label : bizcodename,
				data : businesses[bizcodename]
			};
			index = index + 1;
		}

		$.plot($("#businesses-pie-chart"), data_pie, {
			series : {
				pie : {
					show : true,
					radius : 1,
					label : {
						show : true,
						radius : 2 / 3,
						formatter : function(label, series) {
							return '<div style="font-size:15px;text-align:center;padding:4px;color:white;">' + series.data[0][1] + '</div>';
						},
						threshold : 0.1
					}
				}
			},
			legend : {
				show : true,
				noColumns : 1, // number of colums in legend table
				labelFormatter : null, // fn: string -> string
				labelBoxBorderColor : "#000", // border color for the little label boxes
				container : null, // container (as jQuery object) to put legend in, null means default on top of graph
				position : "ne", // position of default legend container within plot
				margin : [5, 10], // distance from grid edge to default legend container within plot
				backgroundColor : "#efefef", // null means auto-detect
				backgroundOpacity : 1 // set to 0 to avoid background
			},
			grid : {
				hoverable : true,
				clickable : true
			},
		});
	}
	/* end pie chart */
}
