$('.video-container').hide();
$('.contactus-container').hide();
var acc = document.getElementsByClassName("accordion");
var i;
for (i = 0; i < acc.length; i++) {
	acc[i].addEventListener("click", function() {
		this.classList.toggle("active");
		var panel = this.nextElementSibling;
		if (panel.style.display === "block") {
			panel.style.display = "none";
		} else {
			panel.style.display = "block";
		}
	});
}

$('.normalSearch').click(function(e) {
	e.preventDefault();
	var searchText = $('.searchText').val();
	if (searchText !== '' || searchText !== undefined) {
		$.ajax({
			url: "/content/eurail/configuration/submission/searchMovieTrailer",
			method: "GET",
			data: {
				"isNormalSearch": true,
				"searchKeyword": searchText,
				"findIMDBID": true
			},
			dataType: "json",
			error: function() {
				console.log("Error Occured during ajax");
			},
			success: function(response) {
				if (response !== null && response.results !== null && response.results.length > 0) {
					var resp = response.results;
					for (var r in resp) {
						if (resp[r].id !== "" && resp[r].image !== "" && resp[r].title !== "") {
							$('.movie-card-container').append('<div class="movie-card">' +
							    '<img class="poster" src=' + resp[r].image + ' alt=' + resp[r].id + ' />' +
								'<h5 class="movie-title"' + resp[r].title +
								'</h5></div>' +
								'</div>');
						}
					}
					initCustomEvents();
				}
			}
		})
	}
});

var advancedSearchParameter = "";
$('.advancedSearch').click(function(e) {
	e.preventDefault();
	var title_type = "";
	var genres = "";
	$('#advancedSearchForm input').each(
		function(index) {
			var input = $(this);
			var id = '#' + input.attr('id');
			if (input.attr('name').includes('title_type') && $(id).is(':checked')) {
				if (title_type === "") {
					title_type = "title_type=" + input.attr('id');
				} else {
					title_type = title_type + "," + input.attr('id');
				}
			}
			if (input.attr('name').includes('genres') && $(id).is(':checked')) {
				if (genres === "") {
					genres = "genres=" + input.attr('id');
				} else {
					genres = genres + "," + input.attr('id');
				}
			}
		}
	);
	var advancedSearchText = $('.advancedSearchText').val() !== null ? "&title=" + $('.advancedSearchText').val() : "";
	if (title_type !== "") {
		advancedSearchParameter = title_type;
	}
	if (advancedSearchParameter !== "" && genres !== "") {
		advancedSearchParameter = advancedSearchParameter + "&" + genres;
	} else if (advancedSearchParameter === "" && genres !== "") {
		advancedSearchParameter = genres;
	}

	if (advancedSearchText !== "") {
		advancedSearchParameter = advancedSearchParameter + advancedSearchText;
	}
	if (advancedSearchParameter !== "") {
		$.ajax({
			url: "/content/eurail/configuration/submission/searchMovieTrailer",
			method: "GET",
			data: {
				"isNormalSearch": false,
				"searchKeyword": advancedSearchParameter,
				"findIMDBID": true,
			},
			dataType: "json",
			error: function() {
				console.log("Error Occured during ajax");
			},
			success: function(response) {
				if (response !== null && response.results !== null && response.results.length > 0) {
					$('#carousel-example').show();
					var resp = response.results;
					for (var r in resp) {
						if (resp[r].id !== "" && resp[r].image !== "" && resp[r].title !== "") {
							$('.movie-card-container').append('<div class="movie-card">' +
							    '<img class="poster" src=' + resp[r].image + ' alt=' + resp[r].id + ' />' +
								'<h5 class="movie-title"' + resp[r].title +
								'</h5></div>' +
								'</div>');
						}
					}
					initCustomEvents();
				}
			}
		})
	}
});

function initCustomEvents() {
	$('.poster').click(function(e) {
		e.preventDefault();
		var attrValue = $(this).attr("alt");
		$.ajax({
			url: "/content/eurail/configuration/submission/searchMovieTrailer",
			method: "GET",
			data: {
				"isNormalSearch": false,
				"searchKeyword": "",
				"findIMDBID": false,
				"imdbVideoID": attrValue
			},
			method: "GET",
			dataType: "json",
			error: function() {
				console.log("Error Occured during ajax");
			},
			success: function(response) {
				if (response !== null && response.videoUrl) {
					var videoURL = 'https://www.youtube.com/embed/' + response.videoId + '?rel=0';
					$('.video-container').append('<iframe class="embed-responsive-item showVideo" src=' + videoURL + ' allowfullscreen></iframe>');
				}
			}
		})
		$('.video-container').show();
		$('.contactus-container').show();
		$(window).scrollTop($('.video-container').offset().top);
	});
}
