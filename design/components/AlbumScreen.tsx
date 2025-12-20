import { Plus, Filter, Settings } from 'lucide-react';
import { StampCard } from './StampCard';
import type { Stamp } from '../App';
import { useState } from 'react';

interface AlbumScreenProps {
  stamps: Stamp[];
  activeCategory: string;
  onCategoryChange: (category: string) => void;
  onCreateStamp: () => void;
  onStampClick: (stamp: Stamp) => void;
  onOpenSettings: () => void;
  showInfoOnAlbum?: boolean;
}

const categories = ['All', 'Travel', 'Food', 'Daily'];

export function AlbumScreen({
  stamps,
  activeCategory,
  onCategoryChange,
  onCreateStamp,
  onStampClick,
  onOpenSettings,
  showInfoOnAlbum,
}: AlbumScreenProps) {
  const [showFilterMenu, setShowFilterMenu] = useState(false);

  const filteredStamps =
    activeCategory === 'All'
      ? stamps
      : stamps.filter((s) => s.category === activeCategory);

  const handleFilterSelect = (category: string) => {
    onCategoryChange(category);
    setShowFilterMenu(false);
  };

  return (
    <div className="min-h-screen bg-white pb-24">
      {/* Header */}
      <header className="px-6 pt-12 pb-8">
        <div className="flex items-center justify-between mb-8">
          <div className="w-8" /> {/* Spacer for centering */}
          <h1 className="text-center tracking-wide text-gray-900" style={{ fontFamily: 'Georgia, serif' }}>
            Momento
          </h1>
          <div className="flex gap-2">
            <button
              onClick={() => setShowFilterMenu(!showFilterMenu)}
              className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-50 transition-colors relative"
              aria-label="Filter"
            >
              <Filter className="w-4 h-4 text-gray-600" />
            </button>
            <button
              onClick={onOpenSettings}
              className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-50 transition-colors"
              aria-label="Settings"
            >
              <Settings className="w-4 h-4 text-gray-600" />
            </button>
          </div>
        </div>

        {/* Filter Dropdown */}
        {showFilterMenu && (
          <div className="absolute right-6 mt-2 bg-white border border-gray-100 rounded-sm shadow-lg py-2 z-10 min-w-[120px]">
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => handleFilterSelect(category)}
                className={`w-full text-left px-4 py-2 text-sm transition-colors ${
                  activeCategory === category
                    ? 'text-gray-900 bg-gray-50'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                {category}
              </button>
            ))}
          </div>
        )}

        {/* Active Filter Indicator */}
        {activeCategory !== 'All' && (
          <div className="flex items-center justify-center gap-2 mt-2">
            <span className="text-xs text-gray-400">
              Showing: {activeCategory}
            </span>
            <button
              onClick={() => onCategoryChange('All')}
              className="text-xs text-gray-600 underline hover:text-gray-900"
            >
              Clear
            </button>
          </div>
        )}
      </header>

      {/* Stamp Grid */}
      <div className="px-6">
        <div className="grid grid-cols-3 gap-4">
          {filteredStamps.map((stamp) => (
            <StampCard
              key={stamp.id}
              stamp={stamp}
              onClick={() => onStampClick(stamp)}
              showInfo={showInfoOnAlbum}
            />
          ))}
        </div>
      </div>

      {/* Floating Action Button */}
      <button
        onClick={onCreateStamp}
        className="fixed bottom-8 right-6 w-14 h-14 bg-white border border-gray-200 rounded-full flex items-center justify-center shadow-lg hover:shadow-xl hover:border-gray-300 transition-all"
        aria-label="Create new stamp"
      >
        <Plus className="w-6 h-6 text-gray-900" />
      </button>
    </div>
  );
}