$(document).ready(function ($) {

    /**
     * STEP 1
     * cfType selection via clicking on platform images, so show next button.
     */
    $(".platforms").on( "selectableselected", function( event, ui ) {
        // get li corresponding to selected platform (for use below).
        var selectedLi = $(this).find(".ui-selected");
        // get the radio button corresponding to the selected platform.
        var selectedInput = $(this).find(".ui-selected input");

        // uncheck all other platform radio buttons.
        $(".platforms li input").prop("checked", false);
        // make sure all other platform li are not highlighted (workaround for jQuery quirk).
        $(".platforms li").removeClass("ui-selected");
        // unselect any selected cfTypes in the dropdown menu.
        $("#cfType select option:selected").prop("selected", false);

        // check the selected radio button.
        $(selectedInput).prop("checked", true);
        // highlight the selected li
        $(selectedLi).addClass("ui-selected");
        // remove disabled status for submit button.
        $("input[type=submit]").removeAttr("disabled");
        // remove disabled class for submit button.
        $("input[type=submit]").removeClass("disabled");
    } );

    /**
     * STEP 1
     * cfType selection via dropdown menu, so show next button.
     */
    $("#cfType select").change(function( event, ui ) {
        // uncheck all other platform radio buttons.
        $(".platforms li input").prop("checked", false);
        // make sure all other platform li are not highlighted (workaround for jQuery quirk).
        $(".platforms li").removeClass("ui-selected");
        // remove disabled status for submit button.
        $("input[type=submit]").removeAttr("disabled");
        // remove disabled class for submit button.
        $("input[type=submit]").removeClass("disabled");
    });

    /**
     * STEP 2
     * fileType selection via dropdown menu.
     */
    $("select#dataFileType").change(function( event, ui ) {
        var fileType = $(this).find(":selected").text();
        if (fileType == "--") {
            if(!$("#upload").hasClass("hideMe") ) {
                // add hideMe class for file upload section.
                $("#upload").addClass("hideMe");
            }
        } else if (fileType === "Custom File Type") {
            // remove hideMe class for file upload section.
            $("#upload").removeClass("hideMe");
            // remove hideMe class for external positional file upload.
            $("#upload #custom").removeClass("hideMe");
        } else {
            // remove hideMe class for file upload section.
            $("#upload").removeClass("hideMe");
            // add hideMe class for external positional file upload.
            $("#upload #custom").addClass("hideMe");
        }
    });

    /**
     * STEP 2
     * dataFile to upload selected, so show next button.
     */
    $("input:file#dataFile").change(function (){
        var file = $(this)[0].files[0];
        var fileSize = file.size;
        var fileName = file.name;
        // remove disabled status for submit button.
        $("input[type=submit]#next").removeAttr("disabled");
        // remove disabled class for submit button.
        $("input[type=submit]#next").removeClass("disabled");
    });


    /**
     * STEP 3
     * delimiter selected, so show next button.
     */
    $("input#delimiter").change(function (){
        // selected.
        if ($("input#delimiter").is(':checked')) {
            // only show if there is header line data.
            if ($("input#noHeaderLines").is(':checked') || $("input#headerLineNumbers").val()) {
                // remove disabled status for submit button.
                $("input[type=submit]#next").removeAttr("disabled");
                // remove disabled class for submit button.
                $("input[type=submit]#next").removeClass("disabled");
            }
        } else {
            // delimiter unselected for some reason.
            // add disabled status for submit button.
            $("input[type=submit]#next").attr("disabled", true);
            // add disabled class for submit button.
            $("input[type=submit]#next").addClass("disabled");
        }
    });


      function quickSave() {
        $.post("QuickSave", getAllDataInSession(),
            function (data) {
                var info = JSON.parse(data);
                var link = "fileDownload/" + info["uniqueId"] + "/" + info["fileName"];
                // hidden iFrame method based on
                // http://stackoverflow.com/questions/3749231/download-file-using-javascript-jquery/3749395#3749395
                var hiddenIFrame = 'hiddenDownloadFrame',
                       iframe = document.getElementById(hiddenIFrame);
                if (iframe === null) {
                    iframe = document.createElement('iframe');
                    iframe.id = hiddenIFrame;
                    iframe.style.display = 'none';
                    document.body.appendChild(iframe);
                 }
                 iframe.src = link;
               },
           "text");
        ;
      }
});





