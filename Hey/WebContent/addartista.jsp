<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>AddArtista</title>
</head>
<body>
    <s:form action="addartista" method="post">
        <s:text name="Artista:" />
        <s:textfield name="artist_name" /><br>
        <s:text name="Description" />
        <s:textfield name="description" /><br>
        <s:text name="Birth Date (xx.xx.xxxx):" />
        <s:textfield name="birth_date" /><br>
        <s:text name="Group(s) ():" />
        <s:textfield name="groups" /><br>
        <s:submit type="button">
            <s:text name="Add" />
        </s:submit>
    </s:form>
</body>
</html>
