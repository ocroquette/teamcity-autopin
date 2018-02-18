<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<l:settingsGroup title="Conditions">
  <tr>
    <th>Branch must match</th>
    <td>
      <props:textProperty name="branch_pattern"/>
      <span class="smallNote">(optional) Java regular expression that the complete branch name must match</span>
    </td>
  </tr>
</l:settingsGroup>
<l:settingsGroup title="Tagging settings">
  <tr>
    <th>Tag <l:star/></th>
    <td>
      <props:textProperty name="tag"/>
      <span class="error" id="error_tag"></span>
    </td>
  </tr>
  <tr>
    <th>Tag dependencies</th>
    <td>
      <props:checkboxProperty name="tag_dependencies" uncheckedValue="false"/>
      <span class="smallNote">If checked, all build dependencies will be tagged too</span>
    </td>
  </tr>
</l:settingsGroup>
