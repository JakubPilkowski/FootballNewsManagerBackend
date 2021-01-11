-- Role
INSERT INTO public.roles (id, name) VALUES
(1, 'USER'), (2, 'ADMIN'), (3, 'SUPER_ADMIN')
ON CONFLICT DO NOTHING;
-- Strony
INSERT INTO public.sites(id, description,language , popularity, clicks, news_count, logo_url, site_url, name) VALUES
(1, 'Strona piłkarska poświęcona newsom na temat ligi włoskiej', 'English', 0,0,0,'https://www.football-italia.net/sites/all/themes/italia/logo2.png',
 'https://www.football-italia.net','Football Italia'),
(2, 'Strona oferująca dużą bazę informacji na temat najnowszych transferów', 'Polski',0,0,0,'https://transfery.info/img/logo/logo.png',
'https://transfery.info','Transfery.info'),
(3, 'Strona poświęcona najważniejszymi wiadomościami sportowymi.', 'Polski',0,0,0,'https://bi.im-g.pl/im/0/24188/m24188910.png',
'https://www.sport.pl/sport-hp/0,0.html','Sport.pl'),
(4, 'Jedna z największych stron w Polsce poświęcona newsom sportowym. Zawiera newsy z całego świata.', 'Polski',0,0,0,'' ||
 'https://v.wpimg.pl/ODU5LnBuYCU0VzpdbQ5tMHcPbgcrV2NmIBd2TG0Nend6QjwNJhthNDkaPwk1BWB8NlF-XCNGeSJiAnpacEx9dWUHfgh3F3hzYgJ-CHpAdmolWytMPw==',
'https://sportowefakty.wp.pl/pilka-nozna','Sportowe Fakty'),
(5, 'Jedna z największych stron w Polsce poświęcona newsom sportowym. Zawiera newsy z całego świata.', 'Polski',0,0,0,'' ||
'https://sgs.iplsc.com/interia-logo-hd.png',
'https://sport.interia.pl/pilka-nozna','Interia')
ON CONFLICT DO NOTHING;
-- Ligi
INSERT INTO public.leagues(id,apisportid, name, logo_url, type) VALUES
(1,39,'Premier League','https://i.pinimg.com/originals/2a/cc/ee/2accee3ed2f6c675d66001711dcbd1c4.png', 'LIGA'),
(2,140,'LaLiga', 'https://www.realmadryt.pl/static/images/photo/d943c651-6c87-4faa-b86f-41121c377452.png', 'LIGA'),
(3,78,'Bundesliga', 'https://tmssl.akamaized.net/images/logo/originals/l1.png?lm=1525905518', 'LIGA'),
(4,135,'Serie A', 'https://i.pinimg.com/originals/ff/fa/a2/fffaa22a80debb6ce84fa1b1b9cd5cc5.png', 'LIGA'),
(5,61,'Ligue 1', 'https://cdn.bleacherreport.net/images/team_logos/328x328/ligue_1.png', 'LIGA'),
(6,106,'Ekstraklasa', 'https://www.thesportsdb.com/images/media/league/badge/l3jovw1516960585.png', 'LIGA'),
(7,  0,'Fifa', 'https://lofrev.net/wp-content/photos/2014/11/fifa_logo.png', 'REPREZENTACJA'),
(8,  235,'Russian Premier League', 'https://www.wykop.pl/cdn/c3201142/comment_yN28EuuAhpCyfU6jIIAca5bzgxCS6NAE.jpg', 'LIGA'),
(9,  94,'Liga Nos', 'https://www.transfermarkt.pl/images/logo/originals/po1.png?lm=1485174349', 'LIGA'),
(10, 144,'Jupiler Pro League', 'https://upload.wikimedia.org/wikipedia/en/6/67/Belgianproleague.png', 'LIGA'),
(11, 88,'Eredivisie', 'https://1000logos.net/wp-content/uploads/2020/10/Eredivisie-Logo.png', 'LIGA'),
(12, 218,'Austriacka Bundesliga', 'https://vignette.wikia.nocookie.net/logopedia/images/3/30/Logo_for_Austrian_Football_Bundesliga.png/revision/latest?cb=20180412045223', 'LIGA')
ON CONFLICT DO NOTHING;

