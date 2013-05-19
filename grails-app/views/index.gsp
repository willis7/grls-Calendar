<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to Grails</title>
        <r:require module="home" />
	</head>
	<body>
    <div class="nav" role="navigation">
        <ul>
            <li><a href="${createLink(uri: '/')}" class="home">Home</a></li>
            <li><g:link class="calendar" controller="event" action="index">Calendar</g:link></li>
            <li><g:link class="create" controller="event" action="create">New Event</g:link></li>
        </ul>
    </div>

		<div id="page-body" role="main">
			<h1>Environments Planner</h1>
			
            <h2>See the calendar in action</h2>
            <p><g:link action="index" controller="event" >Calendar Demo</g:link>
            

            </div>
	</body>
</html>
