package org.jasig.portal;

import java.util.*;

/**
 * User preferences for stylesheets performing structure transformation
 * @author Peter Kharchenko
 * @version $Revision$
 */


// structure stylesheet preferences will remain to be more complex then 
// preferences of the second stylesheet, hence the derivation 
public class StructureStylesheetUserPreferences extends ThemeStylesheetUserPreferences {
    protected Hashtable folderAttributeNumbers;
    protected Hashtable folderAttributeValues;
    protected ArrayList defaultFolderAttributeValues;

    public StructureStylesheetUserPreferences() {
	super();
	folderAttributeNumbers=new Hashtable();
	folderAttributeValues=new Hashtable();	
	defaultFolderAttributeValues=new ArrayList();
    }

    public StructureStylesheetUserPreferences( StructureStylesheetUserPreferences fsup) {
	super(fsup);
	this.folderAttributeNumbers=new Hashtable(fsup.folderAttributeNumbers);
	this.folderAttributeValues=new Hashtable(fsup.folderAttributeValues);
	this.defaultFolderAttributeValues=new ArrayList(fsup.defaultFolderAttributeValues);
    }

    public String getFolderAttributeValue(String folderID,String attributeName) {
	Integer attributeNumber=(Integer)folderAttributeNumbers.get(attributeName);
	if(attributeNumber==null) {
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::getFolderAttributeValue() : Attempting to obtain a non-existing attribute \""+attributeName+"\".");
	    return null;
	}
	String value=null;
        List l=(List) folderAttributeValues.get(folderID);
	if(l==null) { 
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::getFolderAttributeValue() : Attempting to obtain an attribute for a non-existing folder \""+folderID+"\".");
	    return null;
	} else {
	    try {
		value=(String) l.get(attributeNumber.intValue());
	    } catch (IndexOutOfBoundsException e) {}
	    if(value==null) {
		try {
		    value=(String) defaultFolderAttributeValues.get(attributeNumber.intValue());
		} catch (IndexOutOfBoundsException e) {
		    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::getFolderAttributeValue() : internal error - attribute name is registered, but no default value is provided.");
		    return null;
		}
	    }
	}
	return value;
    }

    // this should be modified to throw exceptions
    public void setFolderAttributeValue(String folderID,String attributeName,String attributeValue) {
	Integer attributeNumber=(Integer)folderAttributeNumbers.get(attributeName);
	if(attributeNumber==null) {
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::setFolderAttribute() : Attempting to set a non-existing folder attribute \""+attributeName+"\".");
	    return;
	}
        List l=(List) folderAttributeValues.get(folderID);
	if(l==null)
	    l=this.createFolder(folderID);
	try {
	    l.set(attributeNumber.intValue(),attributeValue); 
	} catch (IndexOutOfBoundsException e) {
	    // bring up the array to the right size
	    for(int i=l.size();i<attributeNumber.intValue();i++) {
		l.add((String)null);
	    }
	    l.add(attributeValue);
	}
    }

    public void addFolderAttribute(String attributeName, String defaultValue) {
	if(folderAttributeNumbers.get(attributeName)!=null) {
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::addFolderAttribute() : Attempting to re-add an existing folder attribute \""+attributeName+"\".");
	} else {
	    folderAttributeNumbers.put(attributeName,new Integer(defaultFolderAttributeValues.size()));
	    // append to the end of the default value array
	    defaultFolderAttributeValues.add(defaultValue);
	}
    }

    public void setFolderAttributeDefaultValue(String attributeName, String defaultValue) {
	Integer attributeNumber=(Integer)folderAttributeNumbers.get(attributeName);
	defaultFolderAttributeValues.set(attributeNumber.intValue(),defaultValue);
    }

    public void removeFolderAttribute(String attributeName) {
	Integer attributeNumber;
	if((attributeNumber=(Integer)folderAttributeNumbers.get(attributeName))==null) {
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::removeFolderAttribute() : Attempting to remove a non-existing folder attribute \""+attributeName+"\".");
	} else {
	    folderAttributeNumbers.remove(attributeName);
	    // do not touch the arraylists
	}
    }

    public Enumeration getFolderAttributeNames() {
	return folderAttributeNumbers.keys();
    }
    
    public void addFolder(String folderID) {
	// check if the folder is there. In general it might be ok to use this functon to default
	// all of the folder's parameters
	
	ArrayList l=new ArrayList(defaultFolderAttributeValues.size());
	    
	if(folderAttributeValues.put(folderID,l)!=null) 
	    Logger.log(Logger.DEBUG,"StructureStylesheetUserPreferences::addFolder() : Readding an existing folder (folderID=\""+folderID+"\"). All values will be set to default.");
    }
    
    public void removeFolder(String folderID) {
	if(folderAttributeValues.remove(folderID)==null) 
	    Logger.log(Logger.ERROR,"StructureStylesheetUserPreferences::removeFolder() : Attempting to remove an non-existing folder (folderID=\""+folderID+"\").");
    }


    public Enumeration getFolders() {
	return folderAttributeValues.keys();
    }

    public boolean hasFolder(String folderID) {
	return folderAttributeValues.containsKey(folderID);
    }

    private ArrayList createFolder(String folderID) {
	ArrayList l=new ArrayList(defaultFolderAttributeValues.size());
	folderAttributeValues.put(folderID,l);
	return l;
    }

    private Hashtable copyFolderAttributeNames() {
	return folderAttributeNumbers;
    }

    public void synchronizeWithDescription(StructureStylesheetDescription sd) {
	super.synchronizeWithDescription(sd);
	// check if all of the folder attributes in the preferences occur in the description
	for(Enumeration e=folderAttributeNumbers.keys(); e.hasMoreElements(); ) {
	    String pname=(String) e.nextElement();
	    if(!sd.containsFolderAttribute(pname)) {
		this.removeFolderAttribute(pname);
		Logger.log(Logger.DEBUG,"StructureStylesheetUserPreferences::synchronizeWithDescription() : removing folder attribute "+pname);
	    }
	}
	// need to do the reverse synch. here
    }
    
}
