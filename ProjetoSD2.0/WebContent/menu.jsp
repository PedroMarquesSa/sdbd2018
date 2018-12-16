<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hey!</title>
</head>
<body>

	<c:choose>
		<c:when test="${session.loggedin == true}">
			<p>Welcome to DropMusic, ${session.username} .</p>
		</c:when>
		<c:otherwise>
			<p>Welcome, anonymous user.</p>
		</c:otherwise>
	</c:choose>

	<!--BOTOES SEM ACAO-->
	<p><button type="button" onclick="">Adicionar dados</button></p>
	<p><button type="button" onclick="">Modificar dados</button></p>
	<p><button type="button" onclick="">Apagar dados</button></p>
	<p><button type="button" onclick="">Conceder privilegios de editor a um utilizador</button></p>
	<p><a href="<s:url action="goToSearchArtist" />">Consultar detalhes artista</a></p>
	<p><a href="<s:url action="goToSearchAlbumByArtist" />">Pesquisar album por artista</a></p>
	<p><a href="<s:url action="goToSearchAlbumByTitle" />">Pesquisar album por titulo</a></p>
	<p><a href="<s:url action="goToDetailsMusic" />">Consultar detalhes musica</a></p>
	<p><button type="button" onclick="">Pesquisar musica</button></p>
	<p><button type="button" onclick="">Criar playlist</button></p>
	<p><button type="button" onclick="">Editar playlist</button></p>
	<p><a href="<s:url action="goToAlbumCritic" />">Escrever critica a um album</a></p>
	<p><button type="button" onclick="">Upload de ficheiro para associar a uma musica</button></p>

	<p><a href="<s:url action="login" />">Log Out</a></p>

</body>
</html>