<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<l:settingsGroup title="Conditions">
  <tr>
    <th>Build must be</th>
    <td>
      <props:radioButtonProperty name="status_radio" value="Successful"/>Successful<br/>
      <props:radioButtonProperty name="status_radio" value="Failed"/>Failed<br/>
      <props:radioButtonProperty name="status_radio" value="Any"/>Any<br/>
    </td>
  </tr>
  <tr>
    <th>Branch must match</th>
    <td>
      <props:textProperty name="branch_pattern"/>
      <span class="smallNote">(optional) Java regular expression that the complete branch name must match</span>
    </td>
  </tr>
</l:settingsGroup>
<l:settingsGroup title="Pinning settings">
  <tr>
    <th>Pin dependencies</th>
    <td>
      <props:checkboxProperty name="pin_dependencies" uncheckedValue="false"/>
      <span class="smallNote">If checked, all build dependencies will be pinned too</span>
    </td>
  </tr>
  <tr>
    <th>Comment</th>
    <td>
      <props:textProperty name="comment"/>
    </td>
  </tr>
  <tr>
    <th>Add tag</th>
    <td>
      <props:textProperty name="tag"/>
    </td>
  </tr>
</l:settingsGroup>
