function upis() {
    var x = document.forms[0].id;

    var name = document.getElementById(x + ":naziv").value;
    var address = document.getElementById(x + ":adresa").value;

    if (name.length >= 1) {
        if (address.length >= 1) {
            document.getElementById(x + ":upisi_soap").disabled = false;
            document.getElementById(x + ":upisi_rest").disabled = false;
        } else {
            document.getElementById(x + ":upisi_soap").disabled = true;
            document.getElementById(x + ":upisi_rest").disabled = true;
        }
    } else {
        document.getElementById(x + ":upisi_soap").disabled = true;
        document.getElementById(x + ":upisi_rest").disabled = true;
    }
}

function preuzimanje() {
    //<![CDATA[

    var x = document.forms[0].id;

    var select = document.getElementById(x + ":uredaji");
    var count = 0;
    for (var i = 0; i < select.length; i++) {
        if (select[i].selected) {
            count++;
        }
    }

    if (count == 0) {
        document.getElementById(x + ":preuzmi_soap").disabled = true;
        document.getElementById(x + ":preuzmi_rest").disabled = true;
    } else if (count == 1) {
        if (document.getElementById(x + ":vrijeme_od_input").value == "" || document.getElementById(x + ":vrijeme_do_input").value == "") {
            document.getElementById(x + ":preuzmi_soap").disabled = true;
            document.getElementById(x + ":preuzmi_rest").disabled = true;
        } else {
            var vrijeme_od = document.getElementById(x + ":vrijeme_od_input").value;
            var vrijeme_do = document.getElementById(x + ":vrijeme_do_input").value;

            if (vrijeme_od < vrijeme_do) {
                document.getElementById(x + ":preuzmi_soap").disabled = false;
                document.getElementById(x + ":preuzmi_rest").disabled = true;
            } else {
                document.getElementById(x + ":preuzmi_soap").disabled = true;
                document.getElementById(x + ":preuzmi_rest").disabled = true;
            }
        }
    } else {
        document.getElementById(x + ":preuzmi_soap").disabled = true;
        document.getElementById(x + ":preuzmi_rest").disabled = false;
    }

    //]]>
}

$(document).ready(function () {
    upis();
    preuzimanje();
});


