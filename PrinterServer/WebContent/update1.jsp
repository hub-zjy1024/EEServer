<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>欢迎来到大赢家</title>
<link href="css/style.css" rel="stylesheet" type="text/css" media="all" />
<link
	href='http://fonts.useso.com/css?family=Lato:100,300,400,700,900,100italic,300italic,400italic,700italic,900italic'
	rel='stylesheet' type='text/css'>
<link href="css/bootstrap.css" rel="stylesheet" type="text/css"
	media="all" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="baidu" />
<script type="application/x-javascript">
	
	 addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } 

</script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/move-top.js"></script>
<script type="text/javascript" src="js/easing.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		$(".scroll").click(function(event) {
			event.preventDefault();
			$('html,body').animate({
				scrollTop : $(this.hash).offset().top
			}, 1000);
		});
	});
</script>
<!-- start-smoth-scrolling -->

</head>
<body>
	<!-- banner -->
	<div id="home" class="banner">
		<div class="container">
			<div class="banner-info text-center">
				<!-- 	<img src="images/logo.png" alt="" /> -->
				<h2>大赢家</h2>
				<h1>BREATHTAKING APPS</h1>
				<!-- <div class="learnmore">
					<a href="#" class="hvr-sweep-to-right button">LEARN MORE</a>
				</div>
				<div class="arrow-icon">
					<a class="scroll" href="#about"><img src="images/3.png" alt=" " /></a>
				</div> -->
			</div>
		</div>
	</div>

	<!-- /script-for sticky-nav -->
	<!-- //banner -->
	<!-- navigation -->
	<div class="navigation">
		<div class="wrap">
			<div class="fixed-header">
				<div class="nav-left">
					<a href="#"><img src="images/logo2.png" alt="" /></a>
				</div>
				<div class="nav-right">
					<span class="menu"><img src="images/menu.png" alt="" /></span>
					<nav class="cl-effect-1">
						<ul class="nav1">
							<li><a class="scroll" href="#home">主页</a></li>
							<li><a class="scroll" href="#about">下载</a></li>
						</ul>
					</nav>

				</div>
				<div class="clearfix"></div>
			</div>
		</div>
	</div>
	<!-- script for menu -->
	<script>
		$("span.menu").click(function() {
			$("ul.nav1").slideToggle(300, function() {
				// Animation complete.
			});
		});
	</script>
	<!-- //script for menu -->
	<!-- script-for sticky-nav -->
	<script>
		$(document).ready(function() {
			var navoffeset = $(".navigation").offset().top;
			$(window).scroll(function() {
				var scrollpos = $(window).scrollTop();
				if (scrollpos >= navoffeset) {
					$(".navigation").addClass("fixed");
				} else {
					$(".navigation").removeClass("fixed");
				}
			});

		});
	</script>
	<div id="about" class="about">
		<div class="container">
			<div class="col-md-7 about-left">
				<h3>大赢家库房版</h3>
				<div class="strip"></div>
				<h2>
					<a href="download/dyjkf.apk">点击下载app</a>
				</h2>
				<dl>
					<dt>更新日志：</dt>
				</dl>
			</div>
			<div class="col-md-5 about-right text-center">
				<img src="images/5.png" alt="" />
			</div>
			<div class="clearfix"></div>
		</div>
	</div>

	<!-- //contact -->
	<!-- footer -->
	<div class="footer">
		<div class="container">
			<div class="footer-left">
			</div>
			<div class="footer-right">
				<ul>
					<li><a href="#" class="fb"> </a></li>
					<li><a href="#" class="twit"> </a></li>
					<li><a href="#" class="googl"> </a></li>
					<li><a href="#" class="linkin"> </a></li>
				</ul>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	<!-- //footer -->
	<!-- here stars scrolling icon -->
	<script type="text/javascript">
		$(document).ready(function() {
			$().UItoTop({
				easingType : 'easeOutQuart'
			});

		});
	</script>
</body>
</html>