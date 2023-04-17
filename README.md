# PDFs-REST-API

MVC design pattern is used to implement the REST API in Java spring boot. MongoDB and MinIO are used to store the data associated with the Java application. The program works as follows: when a pdf file is received in the controller, it gets parsed, then the metadata will be stored in a collection in Mongo, the sentences will be stored in another collection, while the actual pdf will be stored in MinIO. Then, depending on  the endpoint of the API that gets invoked, the Controller – and with the help of the Services in the Model – will fetch the needed data from these 3 places and a response will be sent to the client after processing the data.<br><br>

You may notice some commented code in MinioService.java which is supposed to set temporary URLs for pdfs stored in MinIO. Since I am using Docker containers to host MinIO, MongoDB and the Java app, and because MinIO is a locally hosted storage, the URL returned from MinioClient cannot be run from the container running the app (the URL is relative to the MinIO container). As a temporary fix, I substitute the endpoint of getting the pdf (/pdf/:id) in the URL’s place. Note, the URL returned from MinioClient should work if you run the services on your machine instead of using Docker.<br>


### To run the app, run the following commands (assuming you have pdfs_app.tar and docker-compose.yaml, if you don't see the end of the README):
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
### If you do not have the pdfs_app.tar you can execute the following on the root directory:

<code>mvn clean package</code><br>
<code>docker build -t pdfs_app .</code><br>
<code>docker-compose up</code>

