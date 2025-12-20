import { useState } from 'react';
import { AlbumScreen } from './components/AlbumScreen';
import { CameraScreen } from './components/CameraScreen';
import { DetailScreen } from './components/DetailScreen';
import { SettingsScreen } from './components/SettingsScreen';

export interface Stamp {
  id: string;
  imageUrl: string;
  date: string;
  location: string;
  category: string;
  memo?: string;
}

type Screen = 'album' | 'camera' | 'detail' | 'settings';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('album');
  const [stamps, setStamps] = useState<Stamp[]>([
    {
      id: '1',
      imageUrl: 'https://images.unsplash.com/photo-1712330138676-60e86456c218?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cmF2ZWwlMjBsYW5kc2NhcGUlMjBzY2VuaWN8ZW58MXx8fHwxNzY2MTE4NDI3fDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.12.15',
      location: 'Swiss Alps',
      category: 'Travel',
      memo: 'Beautiful mountain peaks covered in snow. The air was crisp and fresh.',
    },
    {
      id: '2',
      imageUrl: 'https://images.unsplash.com/photo-1676300185004-c31cf62d3bc8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmb29kJTIwZ291cm1ldCUyMGRpc2h8ZW58MXx8fHwxNzY2MDQxMTg5fDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.12.10',
      location: 'Paris',
      category: 'Food',
      memo: 'Amazing French cuisine at a local bistro. The flavors were unforgettable.',
    },
    {
      id: '3',
      imageUrl: 'https://images.unsplash.com/photo-1758909894264-eae137ed71ca?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb2ZmZWUlMjBtb3JuaW5nJTIwbGlmZXN0eWxlfGVufDF8fHx8MTc2NjExODQyOHww&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.12.18',
      location: 'Home',
      category: 'Daily',
      memo: 'Perfect morning coffee to start the day right.',
    },
    {
      id: '4',
      imageUrl: 'https://images.unsplash.com/photo-1643875402004-22631ef914aa?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjaXR5JTIwYXJjaGl0ZWN0dXJlJTIwdXJiYW58ZW58MXx8fHwxNzY2MDUyMTQzfDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.12.08',
      location: 'New York',
      category: 'Travel',
      memo: 'Urban architecture at its finest. The city never sleeps.',
    },
    {
      id: '5',
      imageUrl: 'https://images.unsplash.com/photo-1666633613714-91e056e7ba1e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxuYXR1cmUlMjBmb3Jlc3QlMjBtb3VudGFpbnxlbnwxfHx8fDE3NjYxMTg0Mjl8MA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.11.28',
      location: 'Oregon',
      category: 'Travel',
      memo: 'Peaceful forest walk surrounded by nature.',
    },
    {
      id: '6',
      imageUrl: 'https://images.unsplash.com/photo-1697809311064-c7a3852206ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiZWFjaCUyMHN1bnNldCUyMG9jZWFufGVufDF8fHx8MTc2NjA1OTE4NXww&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.11.20',
      location: 'Malibu',
      category: 'Travel',
      memo: 'Sunset by the ocean. Pure tranquility.',
    },
    {
      id: '7',
      imageUrl: 'https://images.unsplash.com/photo-1737700087816-95c80570fba8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZXNzZXJ0JTIwcGFzdHJ5JTIwc3dlZXR8ZW58MXx8fHwxNzY2MDYwMDAwfDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.11.15',
      location: 'Vienna',
      category: 'Food',
      memo: 'Traditional Viennese pastries. Heaven on a plate.',
    },
    {
      id: '8',
      imageUrl: 'https://images.unsplash.com/photo-1565560665589-37da0a389949?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzdHJlZXQlMjB1cmJhbiUyMGxpZmVzdHlsZXxlbnwxfHx8fDE3NjYxMTg0MzB8MA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.11.10',
      location: 'Seoul',
      category: 'Daily',
      memo: 'Vibrant street life and urban culture.',
    },
    {
      id: '9',
      imageUrl: 'https://images.unsplash.com/photo-1682888000859-86ed30c13100?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmbG93ZXJzJTIwYm90YW5pY2FsJTIwZ2FyZGVufGVufDF8fHx8MTc2NjExODQzMHww&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.11.05',
      location: 'Kyoto',
      category: 'Travel',
      memo: 'Beautiful botanical garden in full bloom.',
    },
    {
      id: '10',
      imageUrl: 'https://images.unsplash.com/photo-1588420490858-3828a17244a5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx2aW50YWdlJTIwY2FtZXJhJTIwcGhvdG9ncmFwaHl8ZW58MXx8fHwxNzY2MTE4NDMwfDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.10.30',
      location: 'Home',
      category: 'Daily',
      memo: 'My vintage camera collection. Memories captured.',
    },
    {
      id: '11',
      imageUrl: 'https://images.unsplash.com/photo-1706195546853-a81b6a190daf?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxib29rJTIwcmVhZGluZyUyMGNvenl8ZW58MXx8fHwxNzY2MTE4NDMxfDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.10.25',
      location: 'Home',
      category: 'Daily',
      memo: 'Cozy reading time. Lost in a good book.',
    },
    {
      id: '12',
      imageUrl: 'https://images.unsplash.com/photo-1661362758906-3a85700516c6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzdW5zZXQlMjBza3klMjBjbG91ZHN8ZW58MXx8fHwxNzY2MTE1OTIwfDA&ixlib=rb-4.1.0&q=80&w=1080',
      date: '2024.10.20',
      location: 'Santorini',
      category: 'Travel',
      memo: 'Breathtaking sunset views. Pure magic.',
    },
  ]);
  const [selectedStamp, setSelectedStamp] = useState<Stamp | null>(null);
  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [showInfoOnAlbum, setShowInfoOnAlbum] = useState<boolean>(true);
  const [darkMode, setDarkMode] = useState<boolean>(false);
  const [defaultFrameStyle, setDefaultFrameStyle] = useState<string>('Classic');

  const handleCreateStamp = () => {
    setCurrentScreen('camera');
  };

  const handleCaptureStamp = (imageUrl: string) => {
    const newStamp: Stamp = {
      id: Date.now().toString(),
      imageUrl,
      date: new Date().toISOString().split('T')[0].replace(/-/g, '.'),
      location: 'New Location',
      category: activeCategory === 'All' ? 'Daily' : activeCategory,
      memo: '',
    };
    setStamps([newStamp, ...stamps]);
    setCurrentScreen('album');
  };

  const handleStampClick = (stamp: Stamp) => {
    setSelectedStamp(stamp);
    setCurrentScreen('detail');
  };

  const handleBackToAlbum = () => {
    setCurrentScreen('album');
    setSelectedStamp(null);
  };

  const handleUpdateStampMemo = (stampId: string, memo: string) => {
    setStamps(stamps.map(s => s.id === stampId ? { ...s, memo } : s));
    if (selectedStamp?.id === stampId) {
      setSelectedStamp({ ...selectedStamp, memo });
    }
  };

  return (
    <div className="min-h-screen bg-white">
      {currentScreen === 'album' && (
        <AlbumScreen
          stamps={stamps}
          activeCategory={activeCategory}
          onCategoryChange={setActiveCategory}
          onCreateStamp={handleCreateStamp}
          onStampClick={handleStampClick}
          onOpenSettings={() => setCurrentScreen('settings')}
          showInfoOnAlbum={showInfoOnAlbum}
        />
      )}
      {currentScreen === 'camera' && (
        <CameraScreen
          onCapture={handleCaptureStamp}
          onBack={handleBackToAlbum}
        />
      )}
      {currentScreen === 'detail' && selectedStamp && (
        <DetailScreen
          stamp={selectedStamp}
          onBack={handleBackToAlbum}
          onUpdateMemo={handleUpdateStampMemo}
        />
      )}
      {currentScreen === 'settings' && (
        <SettingsScreen
          onBack={handleBackToAlbum}
          showInfoOnAlbum={showInfoOnAlbum}
          onToggleShowInfo={setShowInfoOnAlbum}
          darkMode={darkMode}
          onToggleDarkMode={setDarkMode}
          defaultFrameStyle={defaultFrameStyle}
          onChangeFrameStyle={setDefaultFrameStyle}
        />
      )}
    </div>
  );
}