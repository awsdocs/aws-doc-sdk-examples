function createDiv(data) {
	var data = JSON.parse(data);
	$('#display').html('');
	$('<div class="owl-stage">').appendTo('#display');
	for (var i=0; i<data.length; i++){
		var d = data[i];
		$('<div class="owl-item tile"' + 'id="' + d.item + '">' + d.title + '</div>').appendTo('#display');
	}
	$('</div>').appendTo('#display');
	$(".owl-carousel").owlCarousel({
		responsive : false,
		items : 10
	});
}

function updateRecommendations(url) {
	$.ajax(url, {
		success: function (data, status, xhr) {
			createDiv(data);
		},
		fail: function (data, status, xhr) {
			alert(data);
		}
	});
}



function postEvent(userId, itemId, event) {
	var event = {userId:userId, itemId:itemId, event:event};
	$.ajax({
		  type: "POST",
		  url: '/event',
		  data: JSON.stringify(event)
		});
}
