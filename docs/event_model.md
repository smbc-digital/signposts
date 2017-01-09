## Event mould

![Jelly Mould](jelly_mould.jpg)

Our system allows professionals to search over large numbers of events which may have happened, by searching with demographic data. To allow for:

- The timeline to be drawn
- Drilldown to be carried out
- Access control to be applied
- Matching and graphing to be enhanced in the future

We need for data to fit a certain mould. 

## Typical event

The following illustrates a typical single event with several fields: 

```
{
    "timestamp": "2017-01-09T14:23:19.836Z",
    "name": "Milford Nikolaus",
    "dob": "1996-09-14",
    "address": "270 Kohler Freeway,Somerset,VI8 4XZ",
    "event-source": "SCHOOLS",
    "event-type": "EXCLUSION"
},
   ```
### Timestamp

All data in the system must have a timestamp, otherwise we cannot display it to the user in a sensible way.
   
   
### Demographic fields

The primary purpose of these fields is to allow user searches to match events for a particular individual and for drilldown.
The demographic fields which is searched  must have the same key in all events.

- name
- dob
- address

### Event meta-data

The event meta-data indicated which agency the event occurred in, what sort of event it was, where it occurred (i.e. which agency) and whom to follow up with for more information.


### See also:

* [Splunk Events](https://docs.splunk.com/Splexicon:Event)
* [Palantir Dynamic Ontology](http://about80minutes.blogspot.co.uk/2012/11/palantir-in-number-of-parts-part-2.html)
