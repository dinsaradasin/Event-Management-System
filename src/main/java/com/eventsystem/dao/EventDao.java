package com.eventsystem.dao;

import com.eventsystem.models.*;
import java.util.List;


public interface EventDao {
    List<Event> getEventsForUser(int userId);
    String createNewEvent(String title, String eventDateTime, String location, int capacity, int creatorUserId);
    boolean joinEventWithCode(int userId, String eventCode);
    boolean promoteUserToOrganizer(int eventId, String targetUserEmail);
    int[] getEventAnalytics(int eventId);
    String getEventCode(int eventId);
    List<EventMember> getRecentRegistrations(int eventId);
    List<EventMember> getStaffMembers(int eventId);
    boolean demoteOrganizerToParticipant(int eventId, String targetEmail);
    List<EventMember> getParticipants(int eventId);
    boolean removeParticipant(int eventId, String targetEmail);
    boolean resetEventCode(int eventId);
    boolean insertAgendaItem(int eventId, String title, String startTime, String endTime);
    List<AgendaItem> getAgendaItems(int eventId);
    boolean insertTicketTier(int eventId, String tierName, double price, String description, int totalCapacity, int maxPerUser);
    List<TicketTier> getTicketTiers(int eventId);
    boolean deleteTicketTier(int eventId, int tierId);
    boolean deleteAgendaItem(int agendaId);
    boolean insertAnnouncement(int eventId, String message);
    List<Announcement> getAnnouncements(int eventId);
    boolean deleteAnnouncement(int announcementId);
    boolean purchaseTicket(int userId, int eventId, String tierName, String dietary, String accessibility);
    
   
    List<Ticket> getMyTickets(int userId, int eventId);
    Ticket getMyTicket(String secureToken); 
    
   
    List<EventPreferenceField> getPreferenceFields(int eventId);
    
   
    boolean saveTicketPreferenceAnswer(int ticketId, int fieldId, String answerText); 
   
    String getTicketPreferenceAnswer(int ticketId, int fieldId);
    
    boolean insertPreferenceField(int eventId, String label, String type, String options);
    boolean deletePreferenceField(int fieldId);
  
    boolean togglePreferenceLock(int eventId, boolean lockStatus);
    boolean isPreferenceLocked(int eventId);
   
    List<PreferenceResult> getPreferenceStatistics(int eventId);
   
    String[] getEventDetails(int eventId);
    
    String processDoorCheckIn(int eventId, String secureToken);

    List<GuestDetails> getCheckedInGuests(int eventId);
}