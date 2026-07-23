package com.eventsystem.dao;

import com.eventsystem.models.Event;
import com.eventsystem.models.TicketTier;
import com.eventsystem.models.AgendaItem;
import com.eventsystem.models.EventMember;
import com.eventsystem.models.PreferenceResult;
import com.eventsystem.models.GuestDetails;
import com.eventsystem.models.Announcement;
import com.eventsystem.models.EventPreferenceField;
import com.eventsystem.models.Ticket;
import com.eventsystem.utils.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EventDatabaseActions implements EventDao {

    @Override
    public List<Event> getEventsForUser(int userId) {
        List<Event> userEvents = new ArrayList<>();
        String sqlQuery = "SELECT e.*, m.event_role FROM Events e INNER JOIN Event_Members m ON e.event_id = m.event_id WHERE m.user_id = ?";
        
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
            statement.setInt(1, userId);
            ResultSet results = statement.executeQuery();
            
            while (results.next()) {
                Event foundEvent = new Event(
                    results.getInt("event_id"),
                    results.getString("title"),
                    results.getString("location"),
                    results.getInt("capacity"),
                    results.getString("event_code"),
                    results.getString("event_role") 
                );
                userEvents.add(foundEvent);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Could not load events: " + error.getMessage());
        }
        return userEvents; 
    }

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder uniqueCode = new StringBuilder();
        Random randomGenerator = new Random();
        for (int i = 0; i < 6; i++) {
            uniqueCode.append(characters.charAt(randomGenerator.nextInt(characters.length())));
        }
        return uniqueCode.toString();
    }

    @Override
    public String createNewEvent(String title, String eventDateTime, String location, int capacity, int creatorUserId) {
        String newCode = generateRandomCode();
        String insertEventSql = "INSERT INTO Events (title, event_date, location, capacity, event_code) VALUES (?, ?, ?, ?, ?)";
        String insertMemberSql = "INSERT INTO Event_Members (user_id, event_id, event_role) VALUES (?, ?, 'Admin')";
        
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement eventStatement = myConnection.prepareStatement(insertEventSql, PreparedStatement.RETURN_GENERATED_KEYS);
            eventStatement.setString(1, title);
            eventStatement.setString(2, eventDateTime);
            eventStatement.setString(3, location);
            eventStatement.setInt(4, capacity);
            eventStatement.setString(5, newCode);
            
            int rowsAdded = eventStatement.executeUpdate();
            
            if (rowsAdded > 0) {
                ResultSet generatedKeys = eventStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newlyCreatedEventId = generatedKeys.getInt(1);
                    PreparedStatement memberStatement = myConnection.prepareStatement(insertMemberSql);
                    memberStatement.setInt(1, creatorUserId);
                    memberStatement.setInt(2, newlyCreatedEventId);
                    memberStatement.executeUpdate();
                    
                    myConnection.close();
                    return newCode;
                }
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Failed to create event: " + error.getMessage());
        }
        return null; 
    }

    @Override
    public boolean joinEventWithCode(int userId, String eventCode) {
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            String findEventSql = "SELECT event_id FROM Events WHERE event_code = ?";
            PreparedStatement findStatement = myConnection.prepareStatement(findEventSql);
            findStatement.setString(1, eventCode);
            ResultSet result = findStatement.executeQuery();
            
            if (result.next()) {
                int foundEventId = result.getInt("event_id");
                String insertMemberSql = "INSERT INTO Event_Members (user_id, event_id, event_role) VALUES (?, ?, 'Participant')";
                PreparedStatement joinStatement = myConnection.prepareStatement(insertMemberSql);
                joinStatement.setInt(1, userId);
                joinStatement.setInt(2, foundEventId);
                
                int rowsAdded = joinStatement.executeUpdate();
                if (rowsAdded > 0) {
                    myConnection.close();
                    return true; 
                }
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("SQL ERROR OCCURRED DURING JOIN: " + error.getMessage());
        }
        return false; 
    }

    @Override
    public boolean promoteUserToOrganizer(int eventId, String targetUserEmail) {
        String updateSql = "UPDATE Event_Members SET event_role = 'Organizer' " +
                           "WHERE event_id = ? AND event_role = 'Participant' " +
                           "AND user_id = (SELECT user_id FROM Users WHERE email = ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(updateSql);
            statement.setInt(1, eventId);
            statement.setString(2, targetUserEmail);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                myConnection.close();
                return true;
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Failed to promote user: " + error.getMessage());
        }
        return false;
    }

    @Override
    public int[] getEventAnalytics(int eventId) {
        int[] stats = new int[3]; 
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();

            PreparedStatement ps1 = myConnection.prepareStatement("SELECT capacity FROM Events WHERE event_id = ?");
            ps1.setInt(1, eventId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) stats[0] = rs1.getInt("capacity");

            PreparedStatement ps2 = myConnection.prepareStatement("SELECT COUNT(*) AS total FROM Event_Members WHERE event_id = ?");
            ps2.setInt(1, eventId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) stats[1] = rs2.getInt("total");

            PreparedStatement ps3 = myConnection.prepareStatement("SELECT COUNT(*) AS checked_in FROM Tickets WHERE event_id = ? AND check_in_status = 'Attended'");
            ps3.setInt(1, eventId);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) stats[2] = rs3.getInt("checked_in");
            
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Analytics Error: " + error.getMessage());
        }
        return stats;
    }

    @Override
    public String getEventCode(int eventId) {
        String code = "NOTFOUND";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement("SELECT event_code FROM Events WHERE event_id = ?");
            ps.setInt(1, eventId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                code = rs.getString("event_code"); 
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Code Fetch Error: " + error.getMessage());
        }
        return code;
    }

    @Override
    public List<EventMember> getRecentRegistrations(int eventId) {
        List<EventMember> recentList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, em.event_role FROM Event_Members em " +
                     "JOIN Users u ON em.user_id = u.user_id " +
                     "WHERE em.event_id = ? ORDER BY em.member_id DESC LIMIT 5";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventMember member = new EventMember(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("event_role")
                );
                recentList.add(member);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching recent registrations: " + error.getMessage());
        }
        return recentList;
    }

    @Override
    public List<EventMember> getStaffMembers(int eventId) {
        List<EventMember> staffList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, em.event_role FROM Event_Members em " +
                     "JOIN Users u ON em.user_id = u.user_id " +
                     "WHERE em.event_id = ? AND (em.event_role = 'Admin' OR em.event_role = 'Organizer') " +
                     "ORDER BY em.event_role ASC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventMember staff = new EventMember(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("event_role")
                );
                staffList.add(staff);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Error fetching staff: " + error.getMessage());
        }
        return staffList;
    }

    @Override
    public boolean demoteOrganizerToParticipant(int eventId, String targetEmail) {
        String updateSql = "UPDATE Event_Members SET event_role = 'Participant' " +
                           "WHERE event_id = ? AND event_role = 'Organizer' " +
                           "AND user_id = (SELECT user_id FROM Users WHERE email = ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(updateSql);
            statement.setInt(1, eventId);
            statement.setString(2, targetEmail);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                myConnection.close();
                return true;
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Failed to demote user: " + error.getMessage());
        }
        return false;
    }

    @Override
    public List<EventMember> getParticipants(int eventId) {
        List<EventMember> participantList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, em.event_role FROM Event_Members em " +
                     "JOIN Users u ON em.user_id = u.user_id " +
                     "WHERE em.event_id = ? AND em.event_role = 'Participant' " +
                     "ORDER BY u.name ASC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventMember participant = new EventMember(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("event_role")
                );
                participantList.add(participant);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Error fetching participants: " + error.getMessage());
        }
        return participantList;
    }

    @Override
    public boolean removeParticipant(int eventId, String targetEmail) {
        String sql = "DELETE FROM Event_Members WHERE event_id = ? AND event_role = 'Participant' AND user_id = (SELECT user_id FROM Users WHERE email = ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, eventId);
            statement.setString(2, targetEmail);
            int rowsDeleted = statement.executeUpdate();
            myConnection.close();
            return (rowsDeleted > 0);
        } catch (Exception error) {
            System.out.println("Failed to remove participant: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean resetEventCode(int eventId) {
        String newCode = generateRandomCode(); 
        String sql = "UPDATE Events SET event_code = ? WHERE event_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setString(1, newCode);
            statement.setInt(2, eventId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                myConnection.close();
                return true;
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Failed to reset code: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertAgendaItem(int eventId, String title, String startTime, String endTime) {
        String sql = "INSERT INTO Agenda_Items (event_id, title, start_time, end_time) VALUES (?, ?, ?, ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, eventId);
            statement.setString(2, title);
            statement.setString(3, startTime.replace("T", " ") + ":00");
            statement.setString(4, endTime.replace("T", " ") + ":00");
            int rowsAdded = statement.executeUpdate();
            myConnection.close();
            return rowsAdded > 0;
        } catch (Exception error) {
            System.out.println("Failed to insert agenda item: " + error.getMessage());
        }
        return false;
    }

    @Override
    public List<AgendaItem> getAgendaItems(int eventId) {
        List<AgendaItem> agendaList = new ArrayList<>();
        String sql = "SELECT agenda_id, title, "
                   + "DATE_FORMAT(start_time, '%M %d, %Y') as agenda_date, "
                   + "DATE_FORMAT(start_time, '%h:%i %p') as start_time_formatted, "
                   + "DATE_FORMAT(end_time, '%h:%i %p') as end_time_formatted "
                   + "FROM Agenda_Items WHERE event_id = ? ORDER BY start_time ASC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AgendaItem item = new AgendaItem(
                    rs.getInt("agenda_id"),
                    rs.getString("title"),
                    rs.getString("agenda_date"),
                    rs.getString("start_time_formatted"),
                    rs.getString("end_time_formatted")
                );
                agendaList.add(item);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Error fetching agenda: " + error.getMessage());
        }
        return agendaList;
    }

    @Override
    public boolean insertTicketTier(int eventId, String tierName, double price, String description, int totalCapacity, int maxPerUser) {
        String sql = "INSERT INTO Event_Ticket_Tiers (event_id, tier_name, price, description, total_capacity, max_per_user) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, eventId);
            statement.setString(2, tierName);
            statement.setDouble(3, price);
            statement.setString(4, description);
            statement.setInt(5, totalCapacity);
            statement.setInt(6, maxPerUser);
            int rowsAdded = statement.executeUpdate();
            myConnection.close();
            return rowsAdded > 0;
        } catch (Exception error) {
            System.out.println("Failed to insert ticket tier: " + error.getMessage());
        }
        return false;
    }

    @Override
    public List<TicketTier> getTicketTiers(int eventId) {
        List<TicketTier> tiers = new ArrayList<>();
        String sql = "SELECT t.tier_id, t.tier_name, t.price, t.description, t.total_capacity, t.max_per_user, " +
                     "(SELECT COUNT(*) FROM Tickets tk WHERE tk.event_id = t.event_id AND tk.ticket_tier = t.tier_name) AS sold_count " +
                     "FROM Event_Ticket_Tiers t WHERE t.event_id = ? ORDER BY t.price DESC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TicketTier tier = new TicketTier(
                    rs.getInt("tier_id"),
                    rs.getString("tier_name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("total_capacity"),
                    rs.getInt("max_per_user"),
                    rs.getInt("sold_count") 
                );
                tiers.add(tier);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Error fetching tickets: " + error.getMessage());
        }
        return tiers;
    }

    @Override
    public boolean deleteTicketTier(int eventId, int tierId) {
        String sql = "DELETE FROM Event_Ticket_Tiers WHERE event_id = ? AND tier_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, eventId);
            statement.setInt(2, tierId);
            int rowsDeleted = statement.executeUpdate();
            myConnection.close();
            return rowsDeleted > 0;
        } catch (Exception error) {
            System.out.println("Failed to delete ticket tier: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteAgendaItem(int agendaId) {
        String sql = "DELETE FROM Agenda_Items WHERE agenda_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, agendaId);
            int rowsDeleted = statement.executeUpdate();
            myConnection.close();
            return rowsDeleted > 0;
        } catch (Exception error) {
            System.out.println("Failed to delete agenda item: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertAnnouncement(int eventId, String message) {
        String sql = "INSERT INTO Event_Announcements (event_id, message) VALUES (?, ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sql);
            statement.setInt(1, eventId);
            statement.setString(2, message);
            int rowsAdded = statement.executeUpdate();
            myConnection.close();
            return rowsAdded > 0;
        } catch (Exception error) {
            System.out.println("Failed to insert announcement: " + error.getMessage());
        }
        return false;
    }

    @Override
    public List<Announcement> getAnnouncements(int eventId) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT announcement_id, message, DATE_FORMAT(created_at, '%h:%i %p') as time_sent " +
                     "FROM Event_Announcements WHERE event_id = ? ORDER BY created_at DESC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Announcement ann = new Announcement(
                    rs.getInt("announcement_id"),
                    rs.getString("message"),
                    rs.getString("time_sent")
                );
                announcements.add(ann);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Database Error fetching announcements: " + error.getMessage());
        }
        return announcements;
    }

    @Override
    public boolean deleteAnnouncement(int announcementId) {
        String sql = "DELETE FROM Event_Announcements WHERE announcement_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, announcementId);
            int rowsDeleted = ps.executeUpdate();
            myConnection.close();
            return rowsDeleted > 0;
        } catch (Exception error) {
            System.out.println("Database Error deleting announcement: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean purchaseTicket(int userId, int eventId, String tierName, String dietary, String accessibility) {
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            
            String limitSql = "SELECT total_capacity, max_per_user FROM Event_Ticket_Tiers WHERE event_id = ? AND tier_name = ?";
            PreparedStatement limitPs = myConnection.prepareStatement(limitSql);
            limitPs.setInt(1, eventId);
            limitPs.setString(2, tierName);
            ResultSet limitRs = limitPs.executeQuery();
            
            if (!limitRs.next()) {
                myConnection.close();
                return false; 
            }
            int capacity = limitRs.getInt("total_capacity");
            int maxPerUser = limitRs.getInt("max_per_user");
            
            String countTotalSql = "SELECT COUNT(*) AS total_sold FROM Tickets WHERE event_id = ? AND ticket_tier = ?";
            PreparedStatement totalPs = myConnection.prepareStatement(countTotalSql);
            totalPs.setInt(1, eventId);
            totalPs.setString(2, tierName);
            ResultSet totalRs = totalPs.executeQuery();
            if (totalRs.next() && totalRs.getInt("total_sold") >= capacity) {
                myConnection.close();
                return false; 
            }
            
            String countUserSql = "SELECT COUNT(*) AS user_bought FROM Tickets WHERE user_id = ? AND event_id = ? AND ticket_tier = ?";
            PreparedStatement userPs = myConnection.prepareStatement(countUserSql);
            userPs.setInt(1, userId);
            userPs.setInt(2, eventId);
            userPs.setString(3, tierName);
            ResultSet userRs = userPs.executeQuery();
            if (userRs.next() && userRs.getInt("user_bought") >= maxPerUser) {
                myConnection.close();
                return false; 
            }

            String prefix = tierName.toUpperCase().replaceAll("[^A-Z0-9]", ""); 
            if (prefix.length() > 5) prefix = prefix.substring(0, 5); 
            int randomNum = 1000 + new java.util.Random().nextInt(9000);
            String secureToken = prefix + "-" + randomNum; 
            
            String sql = "INSERT INTO Tickets (user_id, event_id, ticket_tier, secure_token, dietary_restrictions, accessibility_needs) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setString(3, tierName);
            ps.setString(4, secureToken);
            ps.setString(5, dietary);
            ps.setString(6, accessibility);
            int rowsAdded = ps.executeUpdate();
            myConnection.close();
            return rowsAdded > 0;
            
        } catch (Exception error) {
            System.out.println("Error purchasing ticket: " + error.getMessage());
        }
        return false;
    }

    @Override
    public List<Ticket> getMyTickets(int userId, int eventId) {
        List<Ticket> myPurchasedTickets = new ArrayList<>();
        String sql = "SELECT ticket_id, ticket_tier, secure_token, check_in_status " +
                     "FROM Tickets WHERE user_id = ? AND event_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Ticket myTicket = new Ticket(
                    rs.getInt("ticket_id"),
                    eventId,
                    rs.getString("ticket_tier"),
                    rs.getString("secure_token"),
                    rs.getString("check_in_status")
                );
                myPurchasedTickets.add(myTicket); 
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching tickets: " + error.getMessage());
        }
        return myPurchasedTickets; 
    }

    @Override
    public Ticket getMyTicket(String secureToken) {
        String sql = "SELECT ticket_id, event_id, ticket_tier, check_in_status " +
                     "FROM Tickets WHERE secure_token = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setString(1, secureToken);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ticket myTicket = new Ticket(
                    rs.getInt("ticket_id"),
                    rs.getInt("event_id"),
                    rs.getString("ticket_tier"),
                    secureToken,
                    rs.getString("check_in_status")
                );
                myConnection.close();
                return myTicket;
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching ticket by token: " + error.getMessage());
        }
        return null; 
    }
    
    @Override
    public List<EventPreferenceField> getPreferenceFields(int eventId) {
        List<EventPreferenceField> fieldsList = new ArrayList<>();
        String sql = "SELECT * FROM Event_Preference_Fields WHERE event_id = ? ORDER BY field_id ASC";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
              
                EventPreferenceField field = new EventPreferenceField(
                    rs.getInt("field_id"),
                    rs.getInt("event_id"),
                    rs.getString("field_label"),
                    rs.getString("field_type"),
                    rs.getString("dropdown_options")
                );
                fieldsList.add(field);
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching preference fields: " + error.getMessage());
        }
        return fieldsList;
    }

   
    @Override
    public boolean saveTicketPreferenceAnswer(int ticketId, int fieldId, String answerText) {
        String sql = "INSERT INTO Ticket_Preference_Answers (ticket_id, field_id, answer_text) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE answer_text = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, ticketId);
            ps.setInt(2, fieldId);
            ps.setString(3, answerText);
            ps.setString(4, answerText); 
            int rowsAffected = ps.executeUpdate();
            myConnection.close();
            return rowsAffected > 0;
        } catch (Exception error) {
            System.out.println("Error saving ticket preference answer: " + error.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean insertPreferenceField(int eventId, String label, String type, String options) {
        String sql = "INSERT INTO Event_Preference_Fields (event_id, field_label, field_type, dropdown_options) VALUES (?, ?, ?, ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setString(2, label);
            ps.setString(3, type);
            ps.setString(4, options);
            int rowsAdded = ps.executeUpdate();
            myConnection.close();
            return rowsAdded > 0;
        } catch (Exception error) {
            System.out.println("Error inserting preference field: " + error.getMessage());
        }
        return false;
    }

    @Override
    public boolean deletePreferenceField(int fieldId) {
        String sql = "DELETE FROM Event_Preference_Fields WHERE field_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, fieldId);
            int rowsDeleted = ps.executeUpdate();
            myConnection.close();
            return rowsDeleted > 0;
        } catch (Exception error) {
            System.out.println("Error deleting field: " + error.getMessage());
        }
        return false;
    }
    @Override
    public String getTicketPreferenceAnswer(int ticketId, int fieldId) {
        String sql = "SELECT answer_text FROM Ticket_Preference_Answers WHERE ticket_id = ? AND field_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, ticketId);
            ps.setInt(2, fieldId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String savedAnswer = rs.getString("answer_text");
                myConnection.close();
                return savedAnswer; 
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching saved answer: " + error.getMessage());
        }
        return null; 
    }
    @Override
    public boolean togglePreferenceLock(int eventId, boolean lockStatus) {
        String sql = "UPDATE Events SET preferences_locked = ? WHERE event_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setBoolean(1, lockStatus);
            ps.setInt(2, eventId);
            int rows = ps.executeUpdate();
            myConnection.close();
            return rows > 0;
        } catch(Exception e) {
            System.out.println("Error toggling lock: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isPreferenceLocked(int eventId) {
        String sql = "SELECT preferences_locked FROM Events WHERE event_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                boolean isLocked = rs.getBoolean("preferences_locked");
                myConnection.close();
                return isLocked;
            }
            myConnection.close();
        } catch(Exception e) {
            System.out.println("Error checking lock status: " + e.getMessage());
        }
        return false;
    }
    @Override
    public List<PreferenceResult> getPreferenceStatistics(int eventId) {
        List<PreferenceResult> statsList = new ArrayList<>();
        
        
        
        String sql = "SELECT f.field_label, a.answer_text, COUNT(a.answer_id) as vote_count " +
                     "FROM Event_Preference_Fields f " +
                     "LEFT JOIN Ticket_Preference_Answers a ON f.field_id = a.field_id " +
                     "WHERE f.event_id = ? " +
                     "GROUP BY f.field_label, a.answer_text " +
                     "ORDER BY f.field_label ASC, vote_count DESC";
                     
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String label = rs.getString("field_label");
                String answer = rs.getString("answer_text");
                int count = rs.getInt("vote_count");
                
                
                if (answer == null || answer.trim().isEmpty()) {
                    answer = "No Selections Yet";
                }
                
              
                PreferenceResult stat = new PreferenceResult(label, answer, count);
                statsList.add(stat);
            }
            myConnection.close();
        } catch (Exception e) {
            System.out.println("Error fetching preference statistics: " + e.getMessage());
        }
        return statsList;
    }
    @Override
    public String[] getEventDetails(int eventId) {
        
        String[] details = new String[]{"TBD", "TBD", "TBA"}; 
        
        String sql = "SELECT DATE_FORMAT(event_date, '%M %d, %Y') AS pretty_date, " +
                     "DATE_FORMAT(event_date, '%h:%i %p') AS pretty_time, " +
                     "location FROM Events WHERE event_id = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                details[0] = rs.getString("pretty_date");
                details[1] = rs.getString("pretty_time");
                details[2] = rs.getString("location");
            }
            myConnection.close();
        } catch (Exception error) {
            System.out.println("Error fetching event details: " + error.getMessage());
        }
        return details;
    }
    @Override
    public String processDoorCheckIn(int eventId, String secureToken) {
        String checkSql = "SELECT check_in_status FROM Tickets WHERE event_id = ? AND secure_token = ?";
        String updateSql = "UPDATE Tickets SET check_in_status = 'Attended' WHERE event_id = ? AND secure_token = ?";
        
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            
          
            PreparedStatement psCheck = myConnection.prepareStatement(checkSql);
            psCheck.setInt(1, eventId);
            psCheck.setString(2, secureToken);
            ResultSet rs = psCheck.executeQuery();
            
            if (rs.next()) {
                String currentStatus = rs.getString("check_in_status");
                
             
                if ("Attended".equalsIgnoreCase(currentStatus)) {
                    myConnection.close();
                    return "ALREADY_SCANNED";
                }
                
              
                PreparedStatement psUpdate = myConnection.prepareStatement(updateSql);
                psUpdate.setInt(1, eventId);
                psUpdate.setString(2, secureToken);
                psUpdate.executeUpdate();
                
                myConnection.close();
                return "SUCCESS";
            }
            
            myConnection.close();
            return "INVALID_TOKEN"; 
            
        } catch (Exception e) {
            System.out.println("Error processing check-in: " + e.getMessage());
            return "SYSTEM_ERROR";
        }
    }
    @Override
    public List<GuestDetails> getCheckedInGuests(int eventId) {
        List<GuestDetails> guests = new ArrayList<>();
        
      
      
        String sql = "SELECT u.name, t.ticket_tier, t.check_in_status " +
                     "FROM Tickets t " +
                     "LEFT JOIN Users u ON t.user_id = u.user_id " +
                     "WHERE t.event_id = ? " +
                     "ORDER BY u.name ASC";
                     
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement ps = myConnection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            
                String guestName = rs.getString("name");
                String tier = rs.getString("ticket_tier");
                String status = rs.getString("check_in_status");
                
          
                if (guestName == null) guestName = "Unknown User";
                if (tier == null) tier = "Standard Pass";
                if (status == null) status = "Pending";
                
                guests.add(new GuestDetails(guestName, tier, status));
            }
            myConnection.close();
            
        } catch (Exception e) {
            System.out.println("Error fetching guest list: " + e.getMessage());
        }
        return guests;
    }
   
}