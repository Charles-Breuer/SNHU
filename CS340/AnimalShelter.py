from pymongo import MongoClient


class AnimalShelter(object):
    """CRUD operations for working with documents from MongoDB collections"""

    def __init__(self, user, password, DB='AAC', COL='animals'):
        """
        Initializes an instance of AnimalShelter class and establishes a connection to MongoDB.

        Parameters:
        - user: MongoDB username
        - password: MongoDB password
        - DB: Optional, name of the database (default: 'AAC')
        - COL: Optional, name of the collection (default: 'animals')
        """
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 30391

        try:
            self.client = MongoClient('mongodb://%s:%s@%s:%d' % (user, password, HOST, PORT))
            self.database = self.client[DB]
            self.collection = self.database[COL]
        except Exception as e:
            raise ConnectionError("Failed to establish a connection to the database: {}".format(e))

    def create(self, data):
        """
        Inserts a document into the MongoDB collection.

        Parameters:
        - data: A dictionary representing the document to be inserted.

        Returns:
        - True if the document was successfully inserted, False otherwise.
        """
        try:
            if data is not None:
                self.collection.insert_one(data)
                return True
            else:
                return False
        except Exception as e:
            raise ValueError("Failed to create document in the collection: {}".format(e))

    def read(self, query, category = {}):
        """
        Retrieves documents from the MongoDB collection based on a key-value pair.

        Parameters:
        - key: The field to search for.
        - value: The value to match.

        Returns:
        - A list of documents that match the search criteria.
        """
        try:
            if (category == {}):
                documents = list(self.collection.find(query))
            else:
                documents = list(self.collection.find(query, category))
            return documents
        except Exception as e:
            raise ValueError("Failed to read documents from the collection: {}".format(e))

    def update(self, queryKey, queryValue, updateKey, updateValue):
        """
        Updates specified fields in documents that match the query.

        Parameters:
        - query: A dictionary representing the query to find matching documents.
        - update_fields: A dictionary representing the fields to update and their new values.

        Returns:
        - The number of documents modified.
        """
        try:
            query = {queryKey: queryValue}
            updateField = {updateKey: updateValue}
            result = self.collection.update_many(query, {'$set': updateField})
            return result.modified_count
        except Exception as e:
            raise ValueError("Failed to update documents in the collection: {}".format(e))

    def delete(self, key, value):
        """
        Deletes documents from the MongoDB collection based on a key-value pair.

        Parameters:
        - key: The field to search for.
        - value: The value to match.

        Returns:
        - The number of documents removed from the collection.
        """
        try:
            query = {key: value}
            result = self.collection.delete_many(query)
            return result.deleted_count
        except Exception as e:
            raise ValueError("Failed to delete documents from the collection: {}".format(e))
