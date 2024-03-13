package org.ed4becky;

class UserComparator implements java.util.Comparator<User> {
    @Override
    public int compare(User a, User b) {
        int retCode = a.getLastName().compareTo(b.getLastName());
        if(retCode == 0) {
            retCode = a.getFirstName().compareTo(b.getFirstName());
        }
        return retCode;
    }
}