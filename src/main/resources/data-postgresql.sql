-- Role
INSERT INTO public.roles (id, name) VALUES
(1, 'USER'), (2, 'ADMIN'), (3, 'SUPER_ADMIN')
ON CONFLICT DO NOTHING;
-- Strony
INSERT INTO public.sites(id, description, highlighted, popularity, clicks, news_count, chosen_amount, logo_url, site_url, name) VALUES
(1, 'Strona piłkarska poświęcona newsom na temat ligi włoskiej',false, 0,0,0,0,'https://www.football-italia.net/sites/all/themes/italia/logo2.png',
 'https://www.football-italia.net','Football Italia'),
(2, 'Strona oferująca dużą bazę informacji na temat najnowszych transferów',false,0,0,0,0,'https://transfery.info/img/logo/logo.png',
'https://transfery.info','Transfery.info')
ON CONFLICT DO NOTHING;
-- Ligi
INSERT INTO public.leagues(id,apisportid, name, logo_url, type) VALUES
(1,39,'Premier League','https://i.pinimg.com/originals/2a/cc/ee/2accee3ed2f6c675d66001711dcbd1c4.png', 'LIGA'),
(2,140,'LaLiga', 'https://www.realmadryt.pl/static/images/photo/d943c651-6c87-4faa-b86f-41121c377452.png', 'LIGA'),
(3,78,'Bundesliga', 'https://tmssl.akamaized.net/images/logo/originals/l1.png?lm=1525905518', 'LIGA'),
(4,135,'Serie A', 'https://vignette.wikia.nocookie.net/logopedia/images/1/12/Serie_A_2019.svg/revision/latest/scale-to-width-down/340?cb=20190710115458', 'LIGA'),
(5,61,'Ligue 1', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Ligue1_Conforama.svg/203px-Ligue1_Conforama.svg.png', 'LIGA'),
(6,106,'Ekstraklasa', 'https://www.thesportsdb.com/images/media/league/badge/l3jovw1516960585.png', 'LIGA'),
(7,  0,'Reprezentacje', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/FIFA_logo_without_slogan.svg/799px-FIFA_logo_without_slogan.svg.png', 'REPREZENTACJA'),
(8,  235,'Russian Premier League', 'https://www.wykop.pl/cdn/c3201142/comment_yN28EuuAhpCyfU6jIIAca5bzgxCS6NAE.jpg', 'LIGA'),
(9,  94,'Liga Nos', 'https://www.transfermarkt.pl/images/logo/originals/po1.png?lm=1485174349', 'LIGA'),
(10, 144,'Jupiler Pro League', 'https://upload.wikimedia.org/wikipedia/en/6/67/Belgianproleague.png', 'LIGA'),
(11, 88,'Eredivisie', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Eredivisie_nieuw_logo_2017-.svg/1200px-Eredivisie_nieuw_logo_2017-.svg.png', 'LIGA'),
(12, 218,'Austriacka Bundesliga', 'https://vignette.wikia.nocookie.net/logopedia/images/3/30/Logo_for_Austrian_Football_Bundesliga.png/revision/latest?cb=20180412045223', 'LIGA')
ON CONFLICT DO NOTHING;

