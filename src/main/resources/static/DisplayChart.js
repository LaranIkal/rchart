function xmlhttpPostScriptData( strURL, pageForm, iframeField, formOption ) 
{
	var xmlHttpReq = false;
	var self = this;
	var formVariables = "";
        
	var iframeId = document.getElementById( iframeField );
	// Getting form data
	var thisPageForm = pageForm;
	var executeOption = formOption; //"Display Chart";
	
	var ThereIsError = "";
	var ValidationResult = "";

	var chartdata = document.getElementById( 'chartdata' ).value;
	var lsl = document.getElementById( 'lsl' ).value;
	var usl = document.getElementById( 'usl' ).value;
	var rScriptPath = document.getElementById( 'rScriptPath' ).value;
		             
    if( chartdata == "" ) {
        ThereIsError += "<br>Enter comma separated values.";
    }

    if( ThereIsError != "" ) {
        injectHTML( iframeField, "<html><head></head><body><p><b><font color=\"red\">"+ThereIsError+"</font></b></p></body></html>" );
    }
    else {
    		TheTimeOut = setTimeout("document.body.style.cursor='wait'", 1);
    		
            try {		
        		injectHTML( iframeField, "<html><head></head><body><p>Processing, please wait...</p></body></html>" );
        		
                if (window.XMLHttpRequest) { // Mozilla/Safari/Chrome
                    self.xmlHttpReq = new XMLHttpRequest();
                }                                        
                else if (window.ActiveXObject) { // IE
                    self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
                }
            
                self.xmlHttpReq.open( 'POST', strURL, true );
                self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                
                self.xmlHttpReq.onreadystatechange = function() {
                    if( self.xmlHttpReq.readyState == 4 ) {				
						if( self.xmlHttpReq.responseText == "ValidationOk" ) {
							injectHTML( iframeField, "<html><head></head><body><p>Validation Ok...</p></body></html>" );
							thisPageForm.submit();
						}
						else {
							// injectHTML( iframeField, "<html><head></head><body><p>\ntesting text.\n" + self.xmlHttpReq.responseText + "</p></body></html>" );
							injectHTML( iframeField, self.xmlHttpReq.responseText );
							iframeId.style.height = iframeId.contentWindow.document.body.scrollHeight + "px";
							iframeId.style.width = iframeId.contentWindow.document.body.scrollWidth + "px";
							clearTimeout(TheTimeOut); 
							document.body.style.cursor='default';
						} 
                    }
                }
        
                // Getting form variable values and send them. 
                formVariables = 'chartdata=' + escape(chartdata); 
                formVariables += '&lsl=' + escape(lsl);
                formVariables += '&usl=' + escape(usl);
                formVariables += '&rScriptPath=' + escape(rScriptPath);
            
                self.xmlHttpReq.send( formVariables );
            	}
            	catch( err ){ clearTimeout( TheTimeOut ); document.body.style.cursor='default'; }
    		}
}




function injectHTML( iframeField, htmlToInject ){
	 
    //step 1: get the DOM object of the iframe.
    var iframe = document.getElementById( iframeField );
 
    // step 2: obtain the document associated with the iframe tag
    // most of the browser supports .document. 
	// Some support (such as the NetScape series) .contentDocumet, while some 
	// (e.g. IE5/6) support .contentWindow.document
    // we try to read whatever that exists.
    var iframedoc = iframe.document;
    
    if (iframe.contentDocument)
			iframedoc = iframe.contentDocument;
    
    else if (iframe.contentWindow)
      iframedoc = iframe.contentWindow.document;
 
    if (iframedoc){
			// Put the content in the iframe
			iframedoc.open();
			iframedoc.writeln(htmlToInject);
			iframedoc.close();
    } 
	else {
			alert( 'Cannot inject dynamic contents into iframe.' );
    } 
}

	

