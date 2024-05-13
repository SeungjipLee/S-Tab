package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends Neo4jRepository<Note, String> {
    @Query("MATCH (f:Folder)-[:Hierarchy]->(n:Note) WHERE f.id = $folderId AND n.isDeleted = false RETURN n")
    List<Note> findNotesByFolderId(@Param("folderId") String folderId);
    @Query("MATCH (s:Space {id: $spaceId})-[:Hierarchy]->(n:Note) WHERE n.isDeleted = false RETURN n")
    List<Note> findNotesBySpaceId(@Param("spaceId") String spaceId);
    @Query("MATCH (n:Note) WHERE n.id = $noteId RETURN n")
    Note findNoteById(@Param("noteId") String noteId);
    @Query("MATCH (n:Note) WHERE n.isDeleted = true RETURN n")
    List<Note> findDeletedNotes(@Param("userId") long userId);

    @Query("MATCH (u:User {id: $userId})-[:Like]->(n:Note) RETURN n")
    List<Note> findAllLikedNotesByUserId(@Param("userId") long userId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(n:Note {id: $itemId}) DELETE r")
    void deleteLikeNote(@Param("userId") long userId, @Param("itemId") String itemId);

    @Query("MATCH (n:Note {id: $noteId}) SET n.title = $newTitle RETURN n")
    void updateNoteTitle(String noteId, String newTitle);

    @Query("MATCH (p:Page {id: $pageId}) OPTIONAL MATCH path = (p)-[:NextPage*0..]->(firstPage:Page)-[:FirstPage]->(note:Note) RETURN note")
    Note findNoteByPageId(String pageId);
}
