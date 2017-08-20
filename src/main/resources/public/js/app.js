
var map, myPosition;

map = L.map('map').fitWorld();

L.easyButton({
  position:  'bottomright',
  states : [
    {
      icon : 'fa-lg fa-map-marker',
      onClick : function(btn, map){
        createDraggableMarker();
      },
      title : 'Add spot'
    }
  ]
}).addTo(map);

function createDraggableMarker() {
  marker = new L.marker(map.getCenter(), {draggable:'true'});
  marker.on('dragend', function(event){
    var marker = event.target;
    var position = marker.getLatLng();
    marker.setLatLng(new L.LatLng(position.lat, position.lng),{draggable:'true'});
    map.panTo(new L.LatLng(position.lat, position.lng));
    marker.bindPopup(`<button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#spotModal">Add spot here!</button>`);
    marker.openPopup();
  });
  map.addLayer(marker);
}

L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 19,
  attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

map.locate({setView: true, maxZoom: 16});

function onLocationFound(e) {
  console.log("Location found...");
  myPosition = e.latlng;
}

var extractAndDrawSpots = function(data){
  if (data) {
    var spots = JSON.parse(data);
    spots.forEach(function(item) {
      var marker = L.marker([item.location.lat, item.location.lon]).addTo(map).on('click', function(e){
        $.get("/spot-stats/" + item.id, function(data){
          if (data) {
            var stats = JSON.parse(data);
            var distance = 9999999999;
            if (myPosition) {
              distance = myPosition.distanceTo([item.location.lat, item.location.lon]);
            }
            marker.bindPopup(createStatsReport(stats, distance));
            marker.openPopup();
          }
        });
      });
    });
  }

  var createStatsReport = function(stats, distance){
    var lastReportDate = new Date(stats.lastReportDate.iMillis);
    var tooltipMessage =  `Sails from <span class="strong">${stats.lowerSailRange}</span>  to <span class="strong">${stats.upperSailRange}</span> m2` +
      `</br>  boards from  <span class="strong">${stats.lowerBoardRange} to <span class="strong">${stats.upperBoardRange}</span> litres 
            </br> current average rating <span class="strong">${stats.currentRating}</span> </br> last report added: ${lastReportDate} `;
    // if (distance < 1000) {
      tooltipMessage += `</br> <button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#reportModal">Add report</button>`
    // }
    return tooltipMessage;
  };
};

map.on('locationfound', onLocationFound);

map.on('moveend', moveEnd);

function moveEnd(e){
  console.log(e);
  var bounds = e.target.getBounds();
  $.get("/spot?neLat=" + bounds.getNorthEast().lat + "&neLon=" + bounds.getNorthEast().lng + "&swLat=" + bounds.getSouthWest().lat + "&swLon=" + bounds.getSouthWest().lng, function(data){
    console.log(data);
    extractAndDrawSpots(data);
  });
}

function onLocationError(e) {
  alert(e.message);
}

map.on('locationerror', onLocationError);

$("#nav-btn").click(function() {
  $(".navbar-collapse").collapse("toggle");
  return false;
});

$("#searchbox").click(function () {
  $(this).select();
});

/* Prevent hitting enter from refreshing the page */
$("#searchbox").keypress(function (e) {
  if (e.which == 13) {
    e.preventDefault();
  }
});

$(document).one("ajaxStop", function () {
  $("#loading").hide();
  /* instantiate the typeahead UI */

  // build the esri geocoder bloodhound engine
  var esriBH = new Bloodhound({
    name: "esri-geocoder",
    datumTokenizer: function (d) {
      return Bloodhound.tokenizers.whitespace(d.name);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
      //https://developers.arcgis.com/rest/geocode/api-reference/geocoding-suggest.htm
      // use the searchExtent parameter to limit the results to roughly washington state
      url: "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/suggest?text=%QUERY&f=json",
      filter: function (data) {
        return $.map(data.suggestions, function (result) {
          //typically this is a category or type of business (e.g. coffee shops)
          if(result.isCollection === false) {
            return {
              name: result.text,
              magicKey: result.magicKey,
              source: "esri-geocoder"
            };
          }
        });
      },
      ajax: {
        beforeSend: function (jqXhr, settings) {
          $("#searchicon").removeClass("fa-search").addClass("fa-refresh fa-spin");
        },
        complete: function (jqXHR, status) {
          $('#searchicon').removeClass("fa-refresh fa-spin").addClass("fa-search");
        }
      }
    },
    limit: 5
  });
  esriBH.initialize();

  $("#searchbox").typeahead({
    minLength: 3,
    highlight: true,
    hint: false
  }, {
    name: "esri-geocoder",
    displayKey: "name",
    source: esriBH.ttAdapter(),
    templates: {
      header: "<h3 class='typeahead-header'><span class='icon icon-map-marker'></span>&nbsp;Places</h3>"
    }
  }).on("typeahead:selected", function (obj, datum) {

    if (datum.source === "esri-geocoder"){
      var url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/find?" +
        "magicKey=" + datum.magicKey + "&text=" + datum.name +
        "&outFields=Addr_type&f=json";
      // https://developers.arcgis.com/rest/geocode/api-reference/geocoding-find.htm
      // make the call to the geocode service to get the result
      $.getJSON(url, function(result) {
        try {
          if(result.locations.length > 0) {
            var extent = result.locations[0].extent;
            map.fitBounds([[extent.ymin, extent.xmin], [extent.ymax,extent.xmax]]);
          }
          else {
            $("#searchBox").val("Could not go to" + datum.name);
          }
        }
        catch(e) {
          console.log(e);
        }
      });
    }

    if ($(".navbar-collapse").height() > 50) {
      $(".navbar-collapse").collapse("hide");
    }
  }).on("typeahead:opened", function () {
    $(".navbar-collapse.in").css("max-height", $(document).height() - $(".navbar-header").height());
    $(".navbar-collapse.in").css("height", $(document).height() - $(".navbar-header").height());
  }).on("typeahead:closed", function () {
    $(".navbar-collapse.in").css("max-height", "");
    $(".navbar-collapse.in").css("height", "");
  });
  $(".twitter-typeahead").css("position", "static");
  $(".twitter-typeahead").css("display", "block");
});

$(function() {
  return $(".starrr").starrr();
});

$( document ).ready(function() {

  $('#stars').on('starrr:change', function(e, value){
    $('#count').html(value);
  });

  $('#stars-existing').on('starrr:change', function(e, value){
    $('#count-existing').html(value);
  });
});


