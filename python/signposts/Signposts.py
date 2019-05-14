from elasticsearch import Elasticsearch
import uuid

class Signposts:

    def __init__(self, uri, user_id, password):
        self.es = Elasticsearch([uri],http_auth= (user_id, password))
        self.indexes = {}

    def create_index_or_add_event(self, index, document_type ,document_body):
        """Adds document_body"""
        self.es.create(index,id=uuid.uuid4().hex ,doc_type=document_type, body=document_body )

    def search_index(self):
        pass

    def search_indexes(self):
        pass

    def get_index_names(self):
        index_names = self.es.indices()
        return index_names

    def get_document(self, id, index):
        pass



if __name__ == "__main__":
    signposts = Signposts( Elasticsearch('scn-sonardev1:9200','userid','password'))
    signposts

