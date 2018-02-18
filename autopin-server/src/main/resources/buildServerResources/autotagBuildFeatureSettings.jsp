<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<l:settingsGroup title="Conditions">
  <tr>
    <th>Branch must match</th>
    <td>
      <props:textProperty name="branch_pattern"/>
    </td>
  </tr>
</l:settingsGroup>
<l:settingsGroup title="Tagging settings">
  <tr>
    <th>Tag</th>
    <td>
      <props:textProperty name="tag"/>
    </td>
  </tr>
  <tr>
    <th>Tag dependencies</th>
    <td>
      <props:checkboxProperty name="tag_dependencies" uncheckedValue="false"/>
    </td>
  </tr>
</l:settingsGroup>
