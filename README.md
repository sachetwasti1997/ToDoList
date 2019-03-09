# ToDoList
This Android App lets us create a ToDo list, where we can put our tasks according to our priority and delete them when finished.
Also we have the functionality of editing our Tasks if we want to change for example their priorities.
The information entered by user is stored using a database. In this app I provided a contentProvider to handle the actions on
database, ie actions as update, delete, querying the database. But in the working app I focused more in deleting or updating 
an entry in the database using their IDs.
Finally a CursorLoader is used to load the data from the database, which is then displayed in the mobile console using a
RecyclerView.
Fragment is used for displaying the task along with the add, edit screens in case of large screen size.
