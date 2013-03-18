$(document).ready(function() {
    
    $('#newbiz-btn').click(function() {
    	window.open('singlebusiness.jsp', '_self', false);
        return false;
    });
});

function getBusinessTable() 
{
	$.ajax({
        type : "get",
        url : "../businesses",
        success : function(responseJson) {        	
        	var arrayLength = responseJson.length;
        	
        	// Create table itself
            var theTable = document.createElement('table');
            var att=document.createAttribute("class");
            att.value="table table-striped";
            theTable.setAttributeNode(att);
            
            // Create head portion for table
            var thead = document.createElement('thead');
            th_tr = document.createElement('tr');
            th1 = document.createElement('th');
            th1.appendChild(document.createTextNode('Name'));
            th_tr.appendChild(th1);
            th2 = document.createElement('th');
            th2.appendChild(document.createTextNode('Edit Button'));
            th_tr.appendChild(th2);
            thead.appendChild(th_tr);
            theTable.appendChild(thead);
            
            var tbody = document.createElement('tbody');
            
            // Note, don't forget the var keyword!
            for (var i = 0, tr, td; i < arrayLength; i++) {
                tr = document.createElement('tr');
                td = document.createElement('td');
                td.appendChild(document.createTextNode(responseJson[i].name));
                tr.appendChild(td);
                
                // TODO: Actual fields here in the future
                td2 = document.createElement('td');
                td2.appendChild(document.createTextNode('coming soon'));
                tr.appendChild(td2);
                
                tbody.appendChild(tr);
            }
            
            theTable.appendChild(tbody);
          
            document.getElementById('businesslist').appendChild(theTable);	
        }
    });    
}
