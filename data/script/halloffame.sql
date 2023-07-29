-- D  deathmatch
-- M  maze
-- MT best maze time
-- P  paper chase (mine town revival weeks) 2012
-- A  online age
-- T  ATK
-- F  DEF
-- W  wealth (money)
-- X  XP
-- L  level
-- B  Best (xp and online age)
-- C  count reached achievements
-- @  Achievement score
-- R  Role play score (xp, achievement, maze score, & deathmatch score)
-- O
-- 0
-- 3,4,5,6,7,8,9  Paper Chase 2013 and newer
-- S  Sokoban score

SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

UPDATE achievement SET reached =
(SELECT count(*) as cnt
FROM reached_achievement, character_stats
WHERE reached_achievement.achievement_id = achievement.id
AND reached_achievement.charname = character_stats.name
AND character_stats.admin<=6
AND achievement.category != 'SPECIAL'
AND achievement.active=1
GROUP BY achievement_id);
UPDATE achievement SET reached = 0 WHERE reached IS NULL;

INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='MT' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points ASC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'A', @rownum:=@rownum+1 as rankX, age/60, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND age >= 5 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY age DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'T', @rownum:=@rownum+1 as rankX, atk As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'F', @rownum:=@rownum+1 as rankX, def As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'W', @rownum:=@rownum+1 as rankX, money, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND money >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY money DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'X', @rownum:=@rownum+1 as rankX, xp, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND xp >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY xp DESC, karma DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'L', @rownum:=@rownum+1 as rankX, xp, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY xp DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT name, 'B', @rownum:=@rownum+1 as rankX, xp/(age+1) As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND xp >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='C' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rankX, scoretable.points, CURRENT_DATE()
FROM (
  SELECT charname, round(1000000*sum(1/reached)) As points, count(*) As nmb, xp
  FROM achievement
  JOIN reached_achievement ra ON (ra.achievement_id=achievement.id)
  JOIN character_stats ON (name = charname)
  WHERE admin<=6 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
  AND achievement.reached > 0
GROUP BY charname, xp
ORDER BY points DESC, nmb DESC, xp DESC) As scoretable,
(SELECT @rownum:=0) r WHERE points > 500;



INSERT INTO halloffame_archive_recent (charname, fametype, halloffame_archive_recent.rank, points, day)
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rankX, scoretable.points, CURRENT_DATE()
FROM (
  SELECT
    name,
    round(ln(
      (xp
      +ifnull((SELECT points FROM halloffame WHERE fametype='M' AND charname=name LIMIT 1), 0)
      +ifnull((SELECT points FROM halloffame WHERE fametype='D' AND charname=name LIMIT 1), 0)
      )*(ifnull(sum(1/reached)*1000,0)+0.0001))*1000) As points,
    sum(1/reached)
  FROM achievement
  JOIN reached_achievement ra ON (ra.achievement_id=achievement.id)
  RIGHT JOIN character_stats ON name = charname
  WHERE admin<=6 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
  AND achievement.reached > 0
  GROUP BY name
  ORDER BY points desc
) As scoretable,
(SELECT @rownum:=0) r WHERE points > 0;






TRUNCATE TABLE halloffame_archive_alltimes;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='MT' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0
ORDER BY points ASC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 
ORDER BY points;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'A', @rownum:=@rownum+1 as rankX, age/60, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND age >= 5
ORDER BY age DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'T', @rownum:=@rownum+1 as rankX, atk*(1+0.03*level) As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'F', @rownum:=@rownum+1 as rankX, def*(1+0.03*level) As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'W', @rownum:=@rownum+1 as rankX, money, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND money >= 100
ORDER BY money DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'X', @rownum:=@rownum+1 as rankX, xp, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND xp >= 100 
ORDER BY xp DESC, karma DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'L', @rownum:=@rownum+1 as rankX, xp, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND level >= 2
ORDER BY xp DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT name, 'B', @rownum:=@rownum+1 as rankX, xp/(age+1) As points, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=6 AND xp >= 100
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT charname, fametype, @rownum:=@rownum+1 as rankX, points, CURRENT_DATE()
FROM halloffame, character_stats, (SELECT @rownum:=0) r
WHERE fametype='C' AND halloffame.charname = character_stats.name AND admin<=6 AND points > 0 
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rankX, scoretable.points, CURRENT_DATE()
FROM (
  SELECT charname, round(1000000*sum(1/reached)) As points, count(*) As nmb, xp
  FROM achievement
  JOIN reached_achievement ra ON (ra.achievement_id=achievement.id)
  JOIN character_stats ON (name = charname)
  WHERE admin<=6 AND level >= 2
  AND achievement.reached > 0
GROUP BY charname, xp
ORDER BY points DESC, nmb DESC, xp DESC) As scoretable,
(SELECT @rownum:=0) r WHERE points > 500;


INSERT INTO halloffame_archive_alltimes (charname, fametype, halloffame_archive_alltimes.rank, points, day)
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rankX, scoretable.points, CURRENT_DATE()
FROM (
  SELECT
    name,
    round(ln(
      (xp
      +ifnull((SELECT points FROM halloffame WHERE fametype='M' AND charname=name LIMIT 1), 0)
      +ifnull((SELECT points FROM halloffame WHERE fametype='D' AND charname=name LIMIT 1), 0)
      )*(ifnull(sum(1/reached)*1000,0)+0.0001))*1000) As points,
    sum(1/reached)
  FROM achievement
  JOIN reached_achievement ra ON (ra.achievement_id=achievement.id)
  RIGHT JOIN character_stats ON name = charname
  WHERE admin<=6 AND level >= 2
  AND achievement.reached > 0
  GROUP BY name
  ORDER BY points desc
) As scoretable,
(SELECT @rownum:=0) r WHERE points > 0;



-- statistics

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_30', count(DISTINCT player_id), CURRENT_DATE()
FROM loginEvent
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 30 day) AND result=1;

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_7', count(DISTINCT player_id), CURRENT_DATE()
FROM loginEvent
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 7 day) AND result=1;

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_1', count(DISTINCT player_id), CURRENT_DATE()
FROM loginEvent
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 1 day) AND result=1;

CREATE TABLE IF NOT EXISTS cid(charname varchar(32), timedate timestamp);

INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_30', count(DISTINCT charname), CURRENT_DATE()
FROM cid
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 30 day);

INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_7', count(DISTINCT charname), CURRENT_DATE()
FROM cid
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 7 day);

INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_1', count(DISTINCT charname), CURRENT_DATE()
FROM cid
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 1 day);