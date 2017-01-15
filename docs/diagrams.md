  
  
     Canonical Data           Generate and   Transform to Event
     Source Systems            Transport        Model files
  
    +---------------+      +----------------------------------+      +-------+
    | Source System | +--> | BI DW Feed       Transform to EM | +->  |       |
    +---------------+      +----------------------------------+      |       |
                                                                     | Event |
    +---------------+      +---------------+ +----------------+      | Model |
    | Source System | <--+ | SQL on Cron   | |Transform to EM | +->  | Format|
    +---------------+      +---------------+ +----------------+      | Data  |
                                                                     |       |
    +---------------+      +---------------+ +----------------+      | SMBC  |
    | Source System | +--> | Report Email  | |Transform to EM | +->  | File  |
    +---------------+      +---------------+ +----------------+      | Share |
                                                                     |       |
    +-----------------+                                              |       |
                                                                     |       |
    +---------------+      +---------------+ +----------------+      |       |
    | Source System | +--> | Report Email  | |Transform to EM | +->  |       |
    +---------------+      +---------------+ +----------------+      +-------+

         Possible to add an arbitrary number of additional
          external and internal datasources and pipelines
