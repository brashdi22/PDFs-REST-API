# PDFs-REST-API

### To run the app, run the following commands (assuming you have pdfs_app.tar, if you don't see the end of the README):
<ol>
  <li><code>docker load -i pdfs_app.tar</code></li>
  <li><code>docker-compose up</code></li>
</ol>

<br><br>
This starts a MongoDB(port 27017), Mongo-express(port 8081) and MinIO(port 9000) services alongside the application. You can view the Mongo database through Mongo-express in localhost:8081 and you can view MinIO in localhost: 9000 (user:minioroot, password:pass).

<br>

The API is secured with HTTP Basic Authentication. In order to use it you have to register first by sending a POST request to: http://localhost:8080/user/register and passing 2 parameters <i>username</i> and <i>password</i>.
<br>

### A list of the available endpoints:

<table>
  <thead>
    <th>HTTP Method</th>
    <th>Endpoint</th>
    <th>Parameters</th>
    <th>Role</th>
  </thead>
  <tbody>
    <tr>
      <td>POST</td>
      <td>/upload</td>
      <td>'file'=pdf file</td>
      <td>Uploads the pdfs to storage</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/pdfs</td>
      <td>None</td>
      <td>Returns the metadata of the stored pdfs</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/pdf/:id</td>
      <td>None</td>
      <td>Returns a specific pdf - to be downloaded</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/pdf/:id</td>
      <td>'keyword'= a key to count the occurrences of</td>
      <td>Returns the count of the occurrences of the given keyword along with the sentences the keyword appeared in</td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td>/pdf/:id</td>
      <td>None</td>
      <td>Deletes the pdfs</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/pdf/:id/sentences</td>
      <td>None</td>
      <td>Returns the sentences of the pdf</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/pdf/:id/:page</td>
      <td>None</td>
      <td>Returns a specific page of the pdf as a JPEG</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/search</td>
      <td>'keyword'= a word to search for</td>
      <td>Returns the pdfs which contain the given keyword along with the sentences the keyword appeared in</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/pdf/:id/mostCommon</td>
      <td>None</td>
      <td>Returns the top 5 occurring words in a pdf</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/user/register</td>
      <td>'username'=chosen username, 'password'= chosen password</td>
      <td>Registers a new user</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/user/delete</td>
      <td>'username'=your username, 'password'= your password</td>
      <td>Deletes a user</td>
    </tr>
  </tbody>
  
</table>


<br><br>
### To create the pdfs_app.tar you can execute the following on the root directory:

<code>mvn clean package</code><br>
<code>docker build -t pdfs_app .</code>
