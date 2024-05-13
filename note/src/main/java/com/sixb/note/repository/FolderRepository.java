package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import org.springframework.data.repository.query.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, String> {
    @Query("MATCH (p:Folder)-[:Hierarchy]->(c:Folder) WHERE p.folderId = $folderId AND c.isDeleted = false RETURN c")
    List<Folder> findSubFoldersByFolderId(String folderId);
    @Query("MATCH (s:Space {spaceId: $spaceId})-[:Hierarchy]->(f:Folder) WHERE f.isDeleted = false RETURN f")
    List<Folder> findFoldersBySpaceId(@Param("spaceId") String spaceId);

    @Query("MATCH (p:Folder)-[:Hierarchy]->(c:Folder) WHERE c.folderId = $folderId RETURN p")
    Folder findParentFolderByFolderId(@Param("folderId") String folderId);

    @Query("MATCH (f:Folder) WHERE f.isDeleted = true RETURN f")
    List<Folder> findDeletedFolders(@Param("userId") long userId);

    @Query("MATCH (f:Folder) WHERE f.folderId = $folderId RETURN f")
    Folder findFolderById(@Param("folderId") String folderId);

    @Query("MATCH (u:User {userId: $userId})-[:Like]->(f:Folder) RETURN f")
    List<Folder> findAllLikedFoldersByUserId(@Param("userId") long userId);

    @Query("MATCH (u:User {userId: $userId})-[r:Like]->(f:Folder {folderId: $itemId}) DELETE r")
    void deleteLikeFolder(@Param("userId") long userId, @Param("itemId") String itemId);

    @Query("MATCH (f:Folder {folderId: $folderId}) SET f.title = $newTitle RETURN f")
    void updateFolderTitle(String folderId, String newTitle);

    @Query("MATCH (f:Folder {folderId: $folderId})<-[or:Hierarchy]-(op:Folder) " +
            "MATCH (np:Folder {folderId: $parentFolderId}) " +
            "CREATE (f)<-[nr:Hierarchy]-(np) " +
            "DELETE or")
    void relocateFolder(String folderId, String parentFolderId);
}

