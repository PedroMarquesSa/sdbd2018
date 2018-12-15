<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pesquisar album por nome do artista</title>
</head>
<body>
	<s:form action="searchAlbumByArtist" method="post">
		<s:text name="Artista:" />
		<s:textfield name="artist_name" /><br>
		<s:submit type="button">
			<s:text name="Procurar" />
		</s:submit>
	</s:form>
	<br>
	<s:text name="Resposta:" />
	<s:property value="resposta" />
	<br>
	<p><a href="<s:url action="goToMenu" />">Voltar ao menu</a></p>
</body>
</html>