Background & Assumptions

- there is a duty of care to protect vulnerable citizens
- data from multiple agencies is available or can be made available to help with this process

- data needs to be gathered, indexed and made available to relevant users for searching 
- different groups of users (roles) will have authorization to search different subsets of data
- access to data must be audited
- control of data access rules themselves must be audited
- some data may already exist in the data warehouse
- data items are treated as events
- events are unstructured but have certain features
  - when e.g. timestamp, duration
  - who (demographics) e.g. name, date-of-birth, address, postcode, NINO, NHS-ID 
  - what (high-level) e.g. event-source (EIS), event-type (CIN)
  - meta (related, unstructured) e.g. more detail including names of dependents, sub coding of schools exclusion...

- our current hypothesis is that it should be possible to build a useful (MVP) product for the business in 3 months
- we believe that being able to search and surface data, with minimal focus on 'matching' is the quickest route
- there is value in getting something up and running and in front of users before tackling 'matching' enhancements


Architectural Straw Man

Any potential system will be composed of

- ENGINE - a core data store, where data is gathered, secured, and indexed
- INGESTION - a mechanism for loading data into the ENGINE
- UI - the UI allowing search and retrieval from the ENGINE
 
Desirable characteristics of an ENGINE

- can handle unstructured data
- supports unstructured search - ability to search for free text across all data / faceted search in specific fields
- provides fine-grained access control
- ability to replace / remove existing data 
- ability to analyse / process dataset offline for other purposes e.g. inference of groupings / potential links / matches
- scalable - able to store millions of events; able to search rapidly
- Active Directory integration for authentication / authorisation management
- good API support 
  - to simplify connectivity with INGESTION and UI
  - to allow multiple INGESTION, UI technologies

Options:

- existing data warehouse, postgres ? 
- elasticsearch 

Desirable characteristics of UI

- ease of use for untrained staff
- ease of customisation / development of new features
- web ui to simplify deployment
- responsive web ui to facilitate mobile capability ?
- intuitive search building e.g. date range pickers; support for structured query fields
- flexible display e.g. date zooming; inclusion / exclusion of results


Additional Cross Cutting Factors

- Supportability
- Ongoing Maintenance
- Licensing
- Customisation
- Future Requirements
- Skills / Staffing
- DBD Goals / Open Source / Sharing Solutions
