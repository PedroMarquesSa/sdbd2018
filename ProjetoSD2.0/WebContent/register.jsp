<%--
  Created by IntelliJ IDEA.
  User: Pedro
  Date: 12/11/2018
  Time: 6:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Register</title>
</head>
<body>
<s:form action="register" method="post">
    <s:text name="Username:" />
    <s:textfield name="username" /><br>
    <s:text name="Password" />
    <s:textfield name="password" /><br>
    <s:submit type="button">
            <s:text name="Register" />
    </s:submit>
</s:form>
</body>
</html>
